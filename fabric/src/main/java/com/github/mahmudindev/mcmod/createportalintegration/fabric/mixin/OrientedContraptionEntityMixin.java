package com.github.mahmudindev.mcmod.createportalintegration.fabric.mixin;

import com.github.mahmudindev.mcmod.createportalintegration.base.IOrientedContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(OrientedContraptionEntity.class)
public abstract class OrientedContraptionEntityMixin implements IOrientedContraptionEntity {
    @Unique
    private boolean portalSupported;

    @Override
    public boolean createportalintegration$getPortalSupported() {
        return this.portalSupported;
    }

    @Override
    public void createportalintegration$setPortalSupported(boolean portalSupported) {
        this.portalSupported = portalSupported;
    }
}
