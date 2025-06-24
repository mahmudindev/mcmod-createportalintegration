package com.github.mahmudindev.mcmod.createportalintegration.forge.mixin.compat.dimensionlink;

import com.github.mahmudindev.mcmod.createportalintegration.compat.DimensionLinkCompat;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.trains.track.AllPortalTracks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AllPortalTracks.class, priority = 1250)
public abstract class AllPortalTracksHMixin {
    @ModifyExpressionValue(
            method = "nether",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/Level;OVERWORLD:Lnet/minecraft/resources/ResourceKey;"
            )
    )
    private static ResourceKey<Level> netherOverworldKey(
            ResourceKey<Level> original,
            ServerLevel level
    ) {
        return DimensionLinkCompat.getWorldOverworld(level, original);
    }

    @ModifyExpressionValue(
            method = "nether",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/Level;NETHER:Lnet/minecraft/resources/ResourceKey;"
            )
    )
    private static ResourceKey<Level> netherNetherKey(
            ResourceKey<Level> original,
            ServerLevel level
    ) {
        return DimensionLinkCompat.getWorldTheNether(level, original);
    }
}
