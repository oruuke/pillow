package com.oruuke.pillow.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.joml.Vector3d;

import javax.annotation.Nonnull;

public class PillowPlayerData implements Component<EntityStore> {

    // define some vars!
    public Vector3d velocityDelta;

    public static final BuilderCodec<PillowPlayerData> CODEC =
            BuilderCodec.builder(PillowPlayerData.class, PillowPlayerData::new)
                    .append(new KeyedCodec<>("VelocityDelta", Vector3dUtil.CODEC),
                            (data, value) -> data.velocityDelta = value, // setter
                            data -> data.velocityDelta) // getter
                    .addValidator(Validators.nonNull())
                    .add()
                    .build();



    public Vector3d getPillowPlayerData() {
        return this.velocityDelta;
    }
    public void setPillowPlayerData(Vector3d velocityDelta) {
        this.velocityDelta = new Vector3d((double)0.0F, (double)0.0F, (double)0.0F);
    }

    // constructor
    public PillowPlayerData() {
        this.velocityDelta = new Vector3d((double)0.0F, (double)0.0F, (double)0.0F);
    }

    // copy constructor for cloning
    public PillowPlayerData(PillowPlayerData clone) {
        this.velocityDelta = clone.velocityDelta;
    }

    @Nonnull
    @Override
    public Component<EntityStore> clone() {
        return new PillowPlayerData(this);
    }
}