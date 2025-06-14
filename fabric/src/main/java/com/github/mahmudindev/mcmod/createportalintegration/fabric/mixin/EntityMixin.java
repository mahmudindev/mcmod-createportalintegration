package com.github.mahmudindev.mcmod.createportalintegration.fabric.mixin;

import com.github.mahmudindev.mcmod.createportalintegration.base.IAbstractMinecart;
import com.github.mahmudindev.mcmod.createportalintegration.base.IOrientedContraptionEntity;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(
            method = "changeDimension",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;removeAfterChangingDimensions()V"
            )
    )
    private void changeDimensionEntitySetup(
            ServerLevel serverLevel,
            CallbackInfoReturnable<Entity> cir,
            @Local(ordinal = 1) Entity entity
    ) {
        if ((Object) this instanceof IAbstractMinecart entityV) {
            CapabilityMinecartController.tick(serverLevel);

            MinecartController mcA = CapabilityMinecartController.getIfPresent(
                    serverLevel,
                    entity.getUUID()
            );

            UUID coupledCart = entityV.createportalintegration$getCouplingCart();

            if (mcA != null && coupledCart != null) {
                MinecartController mcB = CapabilityMinecartController.getIfPresent(
                        serverLevel,
                        coupledCart
                );
                if (mcB != null) {
                    entity.setDeltaMovement(Vec3.ZERO);
                    serverLevel.resetEmptyTime();

                    Boolean leading = entityV.createportalintegration$getCouplingLead();

                    mcA.prepareForCoupling(leading);
                    mcB.prepareForCoupling(!leading);

                    Float length = entityV.createportalintegration$getCouplingLength();

                    mcA.coupleWith(leading, coupledCart, length, false);
                    mcB.coupleWith(!leading, entity.getUUID(), length, false);
                }
            }

            if (entity.getFirstPassenger() instanceof OrientedContraptionEntity entityX) {
                IOrientedContraptionEntity entityW = (IOrientedContraptionEntity) entityX;
                if (entityW.createportalintegration$getPortalSupported()) {
                    entityW.createportalintegration$setPortalSupported(false);

                    entityX.setPos(entity.getX(), entity.getY(), entity.getZ());
                    serverLevel.addFreshEntity(entityX);
                }
            }

            entityV.createportalintegration$setCouplingCart(null);
            entityV.createportalintegration$setCouplingLead(null);
            entityV.createportalintegration$setCouplingLength(null);
        }
    }
}
