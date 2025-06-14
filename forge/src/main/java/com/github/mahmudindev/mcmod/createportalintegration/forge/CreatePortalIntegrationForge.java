package com.github.mahmudindev.mcmod.createportalintegration.forge;

import com.github.mahmudindev.mcmod.createportalintegration.CreatePortalIntegration;
import net.minecraftforge.fml.common.Mod;

@Mod(CreatePortalIntegration.MOD_ID)
public final class CreatePortalIntegrationForge {
    public CreatePortalIntegrationForge() {
        // Run our common setup.
        CreatePortalIntegration.init();
    }
}
