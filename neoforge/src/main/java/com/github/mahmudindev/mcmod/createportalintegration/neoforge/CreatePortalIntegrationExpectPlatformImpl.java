package com.github.mahmudindev.mcmod.createportalintegration.neoforge;

import net.neoforged.fml.loading.LoadingModList;

public class CreatePortalIntegrationExpectPlatformImpl {
    public static String getPlatformId() {
        return "forge";
    }

    public static boolean isModLoaded(String id) {
        return LoadingModList.get().getModFileById(id) != null;
    }
}
