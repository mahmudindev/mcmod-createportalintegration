package com.github.mahmudindev.mcmod.createportalintegration.mixin;

import com.github.mahmudindev.mcmod.createportalintegration.base.IAbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin implements IAbstractMinecart {
    @Unique
    private UUID couplingCart;
    @Unique
    private Boolean couplingLead;
    @Unique
    private Float couplingLength;

    @Override
    public UUID createportalintegration$getCouplingCart() {
        return this.couplingCart;
    }

    @Override
    public void createportalintegration$setCouplingCart(UUID couplingCart) {
        this.couplingCart = couplingCart;
    }

    @Override
    public Boolean createportalintegration$getCouplingLead() {
        return this.couplingLead;
    }

    @Override
    public void createportalintegration$setCouplingLead(Boolean couplingLead) {
        this.couplingLead = couplingLead;
    }

    @Override
    public Float createportalintegration$getCouplingLength() {
        return this.couplingLength;
    }

    @Override
    public void createportalintegration$setCouplingLength(Float couplingLength) {
        this.couplingLength = couplingLength;
    }
}
