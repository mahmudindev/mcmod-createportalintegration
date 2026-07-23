package com.github.mahmudindev.mcmod.createportalintegration.fabric.mixin.compat.dimensionlink;

import com.github.mahmudindev.mcmod.createportalintegration.compat.DimensionLinkCompat;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.api.contraption.train.PortalTrackProvider;
import com.simibubi.create.content.trains.track.AllPortalTracks;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AllPortalTracks.class, priority = 1250)
public abstract class AllPortalTracksHMixin {
    @WrapMethod(method = "fromPortal", remap = false)
    private static PortalTrackProvider.Exit fromPortalPrepare(
            ServerLevel level,
            BlockFace inboundTrack,
            ResourceKey<Level> firstDimension,
            ResourceKey<Level> secondDimension,
            Portal portal,
            Operation<PortalTrackProvider.Exit> original
    ) {
        if (level.getBlockState(inboundTrack.getConnectedPos()).is(Blocks.NETHER_PORTAL)) {
            firstDimension = DimensionLinkCompat.getWorldOverworld(
                    level,
                    firstDimension
            );
            secondDimension = DimensionLinkCompat.getWorldTheNether(
                    level,
                    secondDimension
            );
        }

        return original.call(level, inboundTrack, firstDimension, secondDimension, portal);
    }
}
