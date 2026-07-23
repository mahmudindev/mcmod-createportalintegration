package com.github.mahmudindev.mcmod.createportalintegration.compat;

import com.github.mahmudindev.mcmod.worldportal.base.IEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class WorldPortalCompat {
    public static ResourceKey<Level> setupPortal(
            Entity entity,
            BlockPos blockPos,
            ResourceKey<Level> resourceKey
    ) {
        return ((IEntity) entity).worldportal$setupPortal(blockPos, resourceKey);
    }
}
