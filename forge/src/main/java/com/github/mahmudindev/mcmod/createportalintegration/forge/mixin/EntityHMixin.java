package com.github.mahmudindev.mcmod.createportalintegration.forge.mixin;

import com.github.mahmudindev.mcmod.createportalintegration.CreatePortalIntegration;
import com.github.mahmudindev.mcmod.createportalintegration.base.IAbstractMinecart;
import com.github.mahmudindev.mcmod.createportalintegration.base.IOrientedContraptionEntity;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.actors.psi.PortableStorageInterfaceMovement;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(value = Entity.class, priority = 1500)
public abstract class EntityHMixin {
    @WrapOperation(
            method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/world/entity/Entity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;unRide()V"
            )
    )
    private void changeDimensionStopUnRide(
            Entity instance,
            Operation<Void> original,
            @Share(
                    namespace = CreatePortalIntegration.MOD_ID + "_Entity",
                    value = "coupledControllers"
            ) LocalRef<List<MinecartController>> coupledControllersRef,
            @Cancellable CallbackInfoReturnable<Entity> cir
    ) {
        if ((Object) this instanceof AbstractMinecart entity)
        //noinspection UnreachableCode
        {
            Level level = entity.level();

            MinecartController mcA = CapabilityMinecartController.getIfPresent(
                    level,
                    entity.getUUID()
            );
            if (mcA != null) {
                if (mcA.isCoupledThroughContraption()) {
                    cir.setReturnValue(null);
                    return;
                }

                List<MinecartController> coupledControllers = new ArrayList<>();
                for (boolean isMain : Iterate.trueAndFalse) {
                    UUID coupledCart = mcA.getCoupledCart(isMain);
                    if (coupledCart == null) {
                        continue;
                    }

                    MinecartController mcB = CapabilityMinecartController.getIfPresent(
                            level,
                            coupledCart
                    );

                    MinecartController mcX = mcB;
                    do {
                        if (mcX.isCoupledThroughContraption()) {
                            cir.setReturnValue(null);
                            return;
                        }

                        mcX = CapabilityMinecartController.getIfPresent(
                                level,
                                mcX.getCoupledCart(isMain)
                        );
                    } while (mcX != null);

                    float length = mcB.getCouplingLength(!isMain);

                    IAbstractMinecart entityX = (IAbstractMinecart) mcB.cart();
                    entityX.createportalintegration$setCouplingLead(!isMain);
                    entityX.createportalintegration$setCouplingLength(length);

                    mcA.removeConnection(isMain);
                    mcB.removeConnection(!isMain);

                    coupledControllers.add(mcB);
                }
                coupledControllersRef.set(coupledControllers);
            }

            if (entity.getFirstPassenger() instanceof OrientedContraptionEntity entityX) {
                IOrientedContraptionEntity entityV = (IOrientedContraptionEntity) entityX;
                entityV.createportalintegration$setPortalSupported(true);
                return;
            }
        }

        original.call(instance);
    }

    @WrapMethod(
            method = "changeDimension(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraftforge/common/util/ITeleporter;)Lnet/minecraft/world/entity/Entity;",
            remap = false
    )
    private Entity changeDimensionPortalSupport(
            ServerLevel serverLevel,
            ITeleporter teleporter,
            Operation<Entity> original,
            @Share(
                    namespace = CreatePortalIntegration.MOD_ID + "_Entity",
                    value = "coupledControllers"
            ) LocalRef<List<MinecartController>> coupledControllersRef
    ) {
        if ((Object) this instanceof AbstractMinecart entity)
        //noinspection UnreachableCode
        {
            Level level = entity.level();

            MinecartController mcA = CapabilityMinecartController.getIfPresent(
                    level,
                    entity.getUUID()
            );
            if (mcA != null) {
                Entity entityX = original.call(serverLevel, teleporter);

                List<MinecartController> coupledControllers = coupledControllersRef.get();
                if (entityX != null && coupledControllers != null) {
                    coupledControllers.forEach(mcB -> {
                        AbstractMinecart entityZ = mcB.cart();

                        entityZ.setPortalCooldown(0);

                        IAbstractMinecart entityV = (IAbstractMinecart) entityZ;
                        entityV.createportalintegration$setCouplingCart(entityX.getUUID());
                    });

                    if (!coupledControllers.isEmpty()) {
                        Vec3 position = entityX.position();
                        for (int i = 0; i < 5; i++) {
                            if (position.distanceTo(entityX.position()) > 1) {
                                break;
                            }

                            entityX.tick();
                        }

                        if (level instanceof ServerLevel serverLevelX) {
                            serverLevelX.getChunkSource().addRegionTicket(
                                    TicketType.PORTAL,
                                    entity.chunkPosition(),
                                    3,
                                    BlockPos.containing(entity.position())
                            );
                        }
                    }
                }

                return entityX;
            }
        } else if ((Object) this instanceof OrientedContraptionEntity entity)
        //noinspection UnreachableCode
        {
            IOrientedContraptionEntity entityA = (IOrientedContraptionEntity) entity;
            if (!entityA.createportalintegration$getPortalSupported()) {
                return original.call(serverLevel, teleporter);
            }

            Contraption contraption = entity.getContraption();
            contraption.stop(entity.level());

            contraption.getActors().forEach(actor -> {
                BlockState blockState = actor.left.state();
                MovementBehaviour mb = MovementBehaviour.REGISTRY.get(blockState);
                if (mb instanceof PortableStorageInterfaceMovement psim) {
                    psim.reset(actor.right);
                }
            });

            CompoundTag contraptionNBT = contraption.writeNBT(false);
            contraptionNBT.remove("UUID");
            contraptionNBT.remove("Pos");
            contraptionNBT.remove("Motion");

            OrientedContraptionEntity entityX = OrientedContraptionEntity.create(
                    serverLevel,
                    Contraption.fromNBT(
                            serverLevel,
                            contraptionNBT,
                            false
                    ),
                    entity.getInitialOrientation()
            );

            entity.discard();

            IOrientedContraptionEntity entityB = (IOrientedContraptionEntity) entityX;
            entityB.createportalintegration$setPortalSupported(true);

            return entityX;
        }

        return original.call(serverLevel, teleporter);
    }
}
