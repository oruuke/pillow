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

public class PillowEffect extends TriggerEffect {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private Vector3d velocity = new Vector3d((double)0.0F, (double)0.0F, (double)0.0F);
    private boolean accumulative = false;
    private Pillow plugin;
    @Nonnull
    private RelativeMode relativeMode;

    public PillowEffect() {
        this.relativeMode = PillowEffect.RelativeMode.ABSOLUTE;
    }

    public static final BuilderCodec<PillowEffect> CODEC = BuilderCodec.builder(PillowEffect.class, PillowEffect::new, BASE_CODEC)
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

    public enum RelativeMode {
        ABSOLUTE,
        HORIZONTAL_FACING,
        FULL_LOOK
    }

    @Nonnull
    public static PillowEffect create(@Nonnull TriggerEventType eventType, @Nonnull Vector3d velocity, boolean accumulative) {
        var effect = new PillowEffect();
        effect.setEventType(eventType);
        effect.velocity = velocity;
        effect.accumulative = accumulative;
        return effect;
    }

    @Override
    public void execute(@Nonnull TriggerContext context) {
        if (velocity == null) return;

        var entityRef = context.getEntityRef();
        var store = context.getStore();
        PillowPlayerData data = store.ensureAndGetComponent(entityRef, Pillow.instance().getPillowPlayerDataComponent());

        var velocityComponent = store.getComponent(entityRef, Velocity.getComponentType());
        if (velocityComponent==null) return;

        var resolvedVelocity = resolveVelocity(context);
        if (resolvedVelocity == null) return;

        if (accumulative) {
            Vector3d newDelta = new Vector3d();
            resolvedVelocity.add(data.getPillowPlayerData(), newDelta);
            velocityComponent.addInstruction(newDelta, null, ChangeVelocityType.Set);
            data.setPillowPlayerData(newDelta);
            LOGGER.at(Level.INFO).log("accumulative delta: %s", newDelta);
        } else {
            velocityComponent.addInstruction(resolvedVelocity, null, ChangeVelocityType.Set);
        }
    }

    @Nullable
    private Vector3d resolveVelocity(@Nonnull TriggerContext context) {

        var mode = relativeMode != null ? relativeMode : RelativeMode.ABSOLUTE;
        if (mode == RelativeMode.ABSOLUTE) {
            return new Vector3d(velocity);
        }

        var rotation = getLookRotation(context);
        if (rotation == null) return null;

        if (mode == RelativeMode.HORIZONTAL_FACING) {
            var horizontal = new Rotation3f(0, rotation.yaw(), 0).transform(new Vector3d(velocity.x, 0, velocity.z));
            horizontal.y = velocity.y;
            return horizontal;
        }



        return rotation.transform(new Vector3d(velocity));
    }

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