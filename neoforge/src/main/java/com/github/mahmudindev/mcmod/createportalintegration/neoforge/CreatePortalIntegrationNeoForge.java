package com.github.mahmudindev.mcmod.createportalintegration.neoforge;

import com.github.mahmudindev.mcmod.createportalintegration.CreatePortalIntegration;
import net.neoforged.fml.common.Mod;

@Mod(CreatePortalIntegration.MOD_ID)
public final class CreatePortalIntegrationNeoForge {
    public CreatePortalIntegrationNeoForge() {
        // Run our common setup.
        CreatePortalIntegration.init();
    }
}
