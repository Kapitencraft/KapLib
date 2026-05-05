package net.kapitencraft.kap_lib.core.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerModConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_ITERATION_BROKEN_BLOCKS = BUILDER
            .comment("determines how many blocks per tick should be broken by the multi-break enchantments")
            .defineInRange("iter_max_broken", 20, 1, 200);
    private static final ModConfigSpec.IntValue ANVIL_LIMIT = BUILDER
            .comment("determines highest anvil experience level before \"Too Expensive\" cap. vanilla default: 40")
            .defineInRange("anvil_limit", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int getMaxBrokenBlocks() {
        return MAX_ITERATION_BROKEN_BLOCKS.get();
    }

    public static int getAnvilLimit() {
        return ANVIL_LIMIT.get();
    }
}
