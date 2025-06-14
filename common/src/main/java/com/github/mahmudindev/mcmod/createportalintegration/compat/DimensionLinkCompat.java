package com.github.mahmudindev.mcmod.createportalintegration.compat;

import com.github.mahmudindev.mcmod.dimensionlink.world.WorldManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class DimensionLinkCompat {
    public static ResourceKey<Level> getWorldOverworld(
            ServerLevel serverLevel,
            ResourceKey<Level> original
    ) {
        return WorldManager.getWorldOverworld(serverLevel, original);
    }

    public static ResourceKey<Level> getWorldTheNether(
            ServerLevel serverLevel,
            ResourceKey<Level> original
    ) {
        return WorldManager.getWorldTheNether(serverLevel, original);
    }
}
