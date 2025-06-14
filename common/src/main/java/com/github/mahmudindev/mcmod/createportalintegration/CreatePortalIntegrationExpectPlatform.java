package com.github.mahmudindev.mcmod.createportalintegration;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class CreatePortalIntegrationExpectPlatform {
    @ExpectPlatform
    public static String getPlatformId() {
        return "";
    }

    @ExpectPlatform
    public static boolean isModLoaded(String id) {
        return false;
    }
}
