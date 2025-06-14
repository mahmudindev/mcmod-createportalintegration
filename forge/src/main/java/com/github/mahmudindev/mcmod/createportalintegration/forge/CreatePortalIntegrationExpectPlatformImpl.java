package com.github.mahmudindev.mcmod.createportalintegration.forge;

import net.minecraftforge.fml.loading.LoadingModList;

public class CreatePortalIntegrationExpectPlatformImpl {
    public static String getPlatformId() {
        return "forge";
    }

    public static boolean isModLoaded(String id) {
        return LoadingModList.get().getModFileById(id) != null;
    }
}
