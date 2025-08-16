package net.kapitencraft.kap_lib.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerModConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLE_SOCIAL_COMMANDS = BUILDER
            .comment("determines if social commands (/show etc.) should be available")
            .define("enable_social", true);
    private static final ModConfigSpec.IntValue MAX_ITERATION_BROKEN_BLOCKS = BUILDER
            .comment("determines how many blocks per tick should be broken by the multi-break enchantments")
            .defineInRange("iter_max_broken", 20, 1, 200);
    private static final ModConfigSpec.BooleanValue DISABLE_ANVIL_LIMIT = BUILDER
            .comment("determines whether to disable anvil \"Too Expensive\" cap")
            .define("disable_anvil_limit", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean areSocialCommandsEnabled() {
        return ENABLE_SOCIAL_COMMANDS.get();
    }

    public static int getMaxBrokenBlocks() {
        return MAX_ITERATION_BROKEN_BLOCKS.get();
    }

    public static boolean disableAnvilLimit() {
        return DISABLE_ANVIL_LIMIT.get();
    }
}
