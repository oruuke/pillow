package com.oruuke.pillow.command;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PillowEnterInstanceCommand extends CommandBase {
    private final RequiredArg<String> nameArg;

    public PillowEnterInstanceCommand() {
        super("enterpillowinstance", "Spawns and enters an instance immediately");
        this.nameArg = this.withRequiredArg("name", "The name of the instance", ArgTypes.STRING);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        UUID playerUUID = ctx.sender().getUuid();
        PlayerRef playerRef = Universe.get().getPlayer(playerUUID);
        World world = Universe.get().getWorld(playerRef.getWorldUuid());

        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
        Transform returnPoint = spawnProvider != null ? spawnProvider.getSpawnPoint(world, playerRef.getUuid()) : new Transform();

        world.execute(() -> {
            //World instanceWorld = InstancesPlugin.get().spawnInstance(this.nameArg.get(ctx), world, returnPoint).join();
            //InstancesPlugin.teleportPlayerToInstance(playerRef.getReference(), playerRef.getReference().getStore(), instanceWorld, (Transform) null);
            CompletableFuture<World> worldFuture = InstancesPlugin.get().spawnInstance(this.nameArg.get(ctx), world, returnPoint);
            InstancesPlugin.teleportPlayerToLoadingInstance(playerRef.getReference(), playerRef.getReference().getStore(), worldFuture, null);
        });
    }
}