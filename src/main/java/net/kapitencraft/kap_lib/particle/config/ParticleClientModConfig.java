package net.kapitencraft.kap_lib.particle.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ParticleClientModConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.comment("Damage Indicator Settings").push("damage_indicator");
        ENABLE_DAMAGE_INDICATOR = BUILDER.comment("whether to enable or disable damage indicators")
                .define("enable_indicator", true);
        DAMAGE_INDICATOR_LIFETIME = BUILDER.comment("how long the damage indicator should live for in ticks")
                .defineInRange("indicator_lifetime", 35, 10, 100);

        SHOW_LIFE_STEAL_PARTICLE = BUILDER
                .comment("determines whether to display life steal particles")
                .define("show_life_steal_particle", true);
    }

    private static final ModConfigSpec.BooleanValue ENABLE_DAMAGE_INDICATOR;
    private static final ModConfigSpec.IntValue DAMAGE_INDICATOR_LIFETIME;

    private static final ModConfigSpec.BooleanValue SHOW_LIFE_STEAL_PARTICLE;

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean isIndicatorEnabled() {
        return ENABLE_DAMAGE_INDICATOR.get();
    }

    public static int getIndicatorLifetime() {
        return DAMAGE_INDICATOR_LIFETIME.get();
    }

    //TODO implement
    public static boolean lifeStealParticleEnabled() {
        return SHOW_LIFE_STEAL_PARTICLE.get();
    }
}