package com.oruuke.pillow.effect;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerContext;
import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class PillowEffect extends TriggerEffect {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private String pillowValue;

    @Override
    public void execute(@Nonnull TriggerContext context) {
        LOGGER.at(Level.INFO).log("triggered: %s", pillowValue);
    }

    public static final BuilderCodec<PillowEffect> CODEC = BuilderCodec.builder(PillowEffect.class, PillowEffect::new, BASE_CODEC)
            .append(new KeyedCodec<>("PillowKey", Codec.STRING), (o, v) -> o.pillowValue = v, o -> o.pillowValue).add()
            .build();
}