package com.github.mahmudindev.mcmod.createportalintegration.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class CreatePortalIntegrationExpectPlatformImpl {
    public static String getPlatformId() {
        return "fabric";
    }

    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }
}
