package com.github.mahmudindev.mcmod.createportalintegration.fabric.mixin.compat.worldportal;

import com.github.mahmudindev.mcmod.createportalintegration.compat.WorldPortalCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.api.contraption.train.PortalTrackProvider;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import com.simibubi.create.content.trains.track.AllPortalTracks;
import net.createmod.catnip.math.BlockFace;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AllPortalTracks.class, priority = 750)
public abstract class AllPortalTracksLMixin {
    @WrapOperation(
            method = "fromProbe",
            at = @At(
                    value = "NEW",
                    target = "com/simibubi/create/content/contraptions/glue/SuperGlueEntity"
            ),
            remap = false
    )
    private static SuperGlueEntity fromProbePrepare(
            Level world,
            AABB boundingBox,
            Operation<SuperGlueEntity> original,
            ServerLevel serverLevel,
            BlockFace inboundTrack,
            @Cancellable CallbackInfoReturnable<PortalTrackProvider.Exit> cir,
            @Local(ordinal = 1) LocalRef<ServerLevel> otherLevel
    ) {
        SuperGlueEntity entity = original.call(world, boundingBox);

        BlockPos blockPos = inboundTrack.getConnectedPos();
        if (serverLevel.getBlockState(blockPos).is(Blocks.NETHER_PORTAL)) {
            MinecraftServer server = serverLevel.getServer();
            ServerLevel serverLevelX = server.getLevel(WorldPortalCompat.getPortalNether(
                    entity,
                    serverLevel,
                    blockPos,
                    otherLevel.get().dimension()
            ));
            if (serverLevelX != null) {
                otherLevel.set(serverLevelX);
            } else {
                cir.setReturnValue(null);
            }
        }

        return entity;
    }
}
