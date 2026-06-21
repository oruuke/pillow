package com.oruuke.pillow.effect;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEventType;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Rotation3fc;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.oruuke.pillow.Pillow;
import com.oruuke.pillow.component.PillowPlayerData;
import org.joml.Vector3d;

import java.util.logging.Level;

// accumulative and reset access for player velocity via stored delta
public class SetPillowEffect extends TriggerEffect {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private Vector3d velocity = new Vector3d((double)0.0F, (double)0.0F, (double)0.0F);
    private boolean accumulative = false;
    private Pillow plugin;
    @Nonnull
    private RelativeMode relativeMode;

    // default to absolute jus makes sense
    public SetPillowEffect() {
        this.relativeMode = SetPillowEffect.RelativeMode.ABSOLUTE;
    }

    // declare trigger volume form fields
    public static final BuilderCodec<SetPillowEffect> CODEC = BuilderCodec.builder(SetPillowEffect.class, SetPillowEffect::new, BASE_CODEC)
            .append(
                    new KeyedCodec<>("Velocity", Vector3dUtil.CODEC),
                    (e, v) -> e.velocity = v,
                    e -> e.velocity
            ).add()
            .append(
                    new KeyedCodec<>("Accumulative", Codec.BOOLEAN, false),
                    (e, v) -> e.accumulative = v,
                    e -> e.accumulative
            ).add()
            .append(
                    new KeyedCodec<>("RelativeMode", new EnumCodec<>(RelativeMode.class), false),
                    (e, v) -> e.relativeMode = v,
                    e -> e.relativeMode
            ).add()
            .build();

    // relative mode options
    public enum RelativeMode {
        ABSOLUTE,
        HORIZONTAL_FACING,
        FULL_LOOK
    }

    // effect thingy
    @Nonnull
    public static SetPillowEffect create(@Nonnull TriggerEventType eventType, @Nonnull Vector3d velocity, boolean accumulative) {
        var effect = new SetPillowEffect();
        effect.setEventType(eventType);
        effect.velocity = velocity;
        effect.accumulative = accumulative;
        return effect;
    }

    // core velocity execution
    @Override
    public void execute(@Nonnull TriggerContext context) {
        if (velocity == null) return;

        // pull out context data
        var entityRef = context.getEntityRef();
        var store = context.getStore();
        PillowPlayerData data = store.ensureAndGetComponent(entityRef, Pillow.instance().getPillowPlayerDataComponent());

        // setup component
        var velocityComponent = store.getComponent(entityRef, Velocity.getComponentType());
        if (velocityComponent==null) return;

        // determine new velocity
        var resolvedVelocity = resolveVelocity(context);
        if (resolvedVelocity == null) return;

        // increase delta for player across all pillow volumes
        if (accumulative) {
            // calculate onto blank vector for clarity
            Vector3d playerVelocity = data.getPillowPlayerData();
            Vector3d newDelta = new Vector3d(
                    accumulateAxis(playerVelocity.x, resolvedVelocity.x),
                    accumulateAxis(playerVelocity.y, resolvedVelocity.y),
                    accumulateAxis(playerVelocity.z, resolvedVelocity.z)
            );
            // send to volume and store on player
            velocityComponent.addInstruction(newDelta, null, ChangeVelocityType.Set);
            data.setPillowPlayerData(newDelta);
            // log all total deltas
            LOGGER.at(Level.INFO).log("x: %s, y: %s, z: %s", newDelta.x, newDelta.y, newDelta.z);
        }
        // set new delta for player
        else {
            velocityComponent.addInstruction(resolvedVelocity, null, ChangeVelocityType.Set);
            data.setPillowPlayerData(resolvedVelocity);
        }
    }

    // compute each player velocity under the same direction as resolved
    private static double accumulateAxis(double player, double resolved) {
        if (resolved == 0.0) {
            return player;
        }
        // ensure new direction is used and clamped at 256 velocity
        double result = resolved + Math.copySign(Math.abs(player), resolved);
        return Math.min(result, 256.0);
    }

    // determine new velocity
    @Nullable
    private Vector3d resolveVelocity(@Nonnull TriggerContext context) {

        // mode stuff
        var mode = relativeMode != null ? relativeMode : RelativeMode.ABSOLUTE;
        if (mode == RelativeMode.ABSOLUTE) {
            return new Vector3d(velocity);
        }

        // rotation funni business
        var rotation = getLookRotation(context);
        if (rotation == null) return null;

        // wotever the heck dis is
        if (mode == RelativeMode.HORIZONTAL_FACING) {
            var horizontal = new Rotation3f(0, rotation.yaw(), 0).transform(new Vector3d(velocity.x, 0, velocity.z));
            horizontal.y = velocity.y;
            return horizontal;
        }

        // i cast: resolve dammit!
        return rotation.transform(new Vector3d(velocity));
    }

    // more rotation spinny magic
    @Nullable
    private static Rotation3fc getLookRotation(@Nonnull TriggerContext context) {
        var store = context.getStore();
        var entityRef = context.getEntityRef();

        var headRotation = store.getComponent(entityRef, HeadRotation.getComponentType());
        if (headRotation != null) return headRotation.getRotation();

        var transform = store.getComponent(entityRef, TransformComponent.getComponentType());
        return transform != null ? transform.getRotation() : null;
    }
}