package com.github.mahmudindev.mcmod.createportalintegration.compat;

import com.github.mahmudindev.mcmod.worldportal.base.IEntity;
import com.github.mahmudindev.mcmod.worldportal.base.IServerLevel;
import com.github.mahmudindev.mcmod.worldportal.portal.PortalData;
import com.github.mahmudindev.mcmod.worldportal.portal.PortalManager;
import com.github.mahmudindev.mcmod.worldportal.portal.PortalReturns;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.Map;

public class WorldPortalCompat {
    public static ResourceKey<Level> getPortalNether(
            Entity entity,
            ServerLevel serverLevel,
            BlockPos blockPos,
            ResourceKey<Level> original
    ) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        if (!blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            return original;
        }

        Direction.Axis axis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        BlockUtil.FoundRectangle foundRectangle = BlockUtil.getLargestRectangleAround(
                blockPos,
                axis,
                21,
                Direction.Axis.Y,
                21,
                blockPosX -> serverLevel.getBlockState(blockPosX) == blockState
        );

        BlockPos minCornerPos = foundRectangle.minCorner;

        ResourceLocation frameC1 = BuiltInRegistries.BLOCK.getKey(
                serverLevel.getBlockState(minCornerPos.offset(
                        axis == Direction.Axis.X ? -1 : 0,
                        -1,
                        axis == Direction.Axis.Z ? -1 : 0
                )).getBlock()
        );
        ResourceLocation frameC2 = BuiltInRegistries.BLOCK.getKey(
                serverLevel.getBlockState(minCornerPos.offset(
                        axis == Direction.Axis.X ? foundRectangle.axis1Size : 0,
                        -1,
                        axis == Direction.Axis.Z ? foundRectangle.axis1Size : 0
                )).getBlock()
        );
        ResourceLocation frameC3 = BuiltInRegistries.BLOCK.getKey(
                serverLevel.getBlockState(minCornerPos.offset(
                        axis == Direction.Axis.X ? -1 : 0,
                        foundRectangle.axis2Size,
                        axis == Direction.Axis.Z ? -1 : 0
                )).getBlock()
        );
        ResourceLocation frameC4 = BuiltInRegistries.BLOCK.getKey(
                serverLevel.getBlockState(minCornerPos.offset(
                        axis == Direction.Axis.X ? foundRectangle.axis1Size : 0,
                        foundRectangle.axis2Size,
                        axis == Direction.Axis.Z ? foundRectangle.axis1Size : 0
                )).getBlock()
        );

        Map<ResourceLocation, PortalData> portals = new HashMap<>();

        PortalManager.getPortals().forEach((k, v) -> {
            ResourceLocation c1 = v.getFrameBottomLeftLocation();
            if (c1 != null && !frameC1.equals(c1)) {
                return;
            }

            ResourceLocation c2 = v.getFrameBottomRightLocation();
            if (c2 != null && !frameC2.equals(c2)) {
                return;
            }

            ResourceLocation c3 = v.getFrameTopLeftLocation();
            if (c3 != null && !frameC3.equals(c3)) {
                return;
            }

            ResourceLocation c4 = v.getFrameTopRightLocation();
            if (c4 != null && !frameC4.equals(c4)) {
                return;
            }

            portals.put(k, v);
        });

        ResourceKey<Level> dimension = serverLevel.dimension();

        IServerLevel serverLevelX = (IServerLevel) serverLevel;
        PortalReturns portalReturns = serverLevelX.worldportal$getPortalReturns();

        if (!portals.isEmpty()) {
            ResourceKey<Level> returnDimension = portalReturns.getDimension(minCornerPos);
            if (returnDimension != null) {
                for (Map.Entry<ResourceLocation, PortalData> entry : portals.entrySet()) {
                    if (dimension != entry.getValue().getDestinationKey()) {
                        continue;
                    }

                    ((IEntity) entity).worldportal$setPortal(entry.getKey());

                    return returnDimension;
                }
            }
        }

        portals.keySet().removeIf(k -> dimension == portals.get(k).getDestinationKey());

        if (!portals.isEmpty()) {
            int random = serverLevel.getRandom().nextInt(portals.size());

            int i = 0;
            for (Map.Entry<ResourceLocation, PortalData> entry : portals.entrySet()) {
                if (i != random) {
                    i++;

                    continue;
                }

                ResourceKey<Level> modified = entry.getValue().getDestinationKey();
                if (modified != null) {
                    ((IEntity) entity).worldportal$setPortal(entry.getKey());

                    return modified;
                }
            }
        }

        portalReturns.removeDimension(minCornerPos);

        return original;
    }
}
