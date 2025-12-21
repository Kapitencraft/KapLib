package net.kapitencraft.kap_lib.core;

import net.neoforged.fml.ModList;

public class Modules {

    public static boolean isRequirementsActive() {
        ModList list = ModList.get();
        return list.isLoaded("kap_lib") || list.isLoaded("kap_lib_requirement");
    }

    public static boolean isManaActive() {
        ModList list = ModList.get();
        return list.isLoaded("kap_lib") || list.isLoaded("kap_lib_mana");
    }

    public static boolean isAttributesActive() {
        ModList list = ModList.get();
        return list.isLoaded("kap_lib") || list.isLoaded("kap_lib_attribute");
    }
}
