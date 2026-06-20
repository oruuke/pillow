package com.oruuke.pillow;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.oruuke.pillow.command.PillowEnterInstanceCommand;
import com.oruuke.pillow.effect.PillowEffect;

import java.util.logging.Level;

public class Pillow extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public Pillow(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("Starting Pillow!");
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Setting up Pillow!");
        this.getCommandRegistry().registerCommand(new PillowEnterInstanceCommand());
        TriggerEffect.CODEC.register("PillowEffect", PillowEffect.class, PillowEffect.CODEC);
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("Shutting down Pillow!");
    }
}
