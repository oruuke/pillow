package com.oruuke.pillow;

import com.hypixel.hytale.builtin.triggervolumes.effect.TriggerEffect;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.oruuke.pillow.command.PillowEnterInstanceCommand;
import com.oruuke.pillow.component.PillowPlayerData;
import com.oruuke.pillow.effect.SetPillowEffect;

import java.util.logging.Level;

public class Pillow extends JavaPlugin {
    private ComponentType<EntityStore, PillowPlayerData> pillowPlayerDataComponent;
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static Pillow instance;

    public Pillow(JavaPluginInit init) {
        super(init);
        instance = this;
    }
    public static Pillow instance() {
        return instance;
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("Starting Pillow!");
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Setting up Pillow!");
        this.getCommandRegistry().registerCommand(new PillowEnterInstanceCommand());
        TriggerEffect.CODEC.register("SetPillow", SetPillowEffect.class, SetPillowEffect.CODEC);
        this.pillowPlayerDataComponent = this.getEntityStoreRegistry().registerComponent(
                PillowPlayerData.class,
                "PillowPlayerDataComponent",
                PillowPlayerData.CODEC
        );
    }

    public ComponentType<EntityStore, PillowPlayerData> getPillowPlayerDataComponent() {
        return this.pillowPlayerDataComponent;
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("Shutting down Pillow!");
    }
}
