package net.kapitencraft.kap_lib.enchantment.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EnchantmentClientModConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {

        BUILDER.comment("configuration for enchantment display").push("enchantment");
        SHOW_ENCHANTMENT_OBTAIN_DISPLAY = BUILDER.comment("whether to show the enchantments obtain display", "e.g. if it's a treasure enchantment or can't be traded with villagers", "only shows on books")
                .define("obtain_display", true);
        SHOW_ENCHANTMENT_APPLICABLE = BUILDER.comment("whether to show the items a given enchantment can be applied to", "only shows on books")
                .define("show_applicable", true);

        BUILDER.pop();
    }

    private static final ModConfigSpec.BooleanValue SHOW_ENCHANTMENT_OBTAIN_DISPLAY;
    private static final ModConfigSpec.BooleanValue SHOW_ENCHANTMENT_APPLICABLE;

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean showObtainDisplay() {
        return SHOW_ENCHANTMENT_OBTAIN_DISPLAY.get();
    }

    public static boolean showApplyDisplay() {
        return SHOW_ENCHANTMENT_APPLICABLE.get();
    }
}