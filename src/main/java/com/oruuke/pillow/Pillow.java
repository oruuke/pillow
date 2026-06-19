package com.oruuke.pillow;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

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
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("Shutting down Pillow!");
    }
}
