package net.kapitencraft.kap_lib.config;

import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class ServerModConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENABLE_SOCIAL_COMMANDS = BUILDER
            .comment("determines if social commands (/show etc.) should be available")
            .define("enable_social", true);
    private static final ForgeConfigSpec.IntValue MAX_ITERATION_BROKEN_BLOCKS = BUILDER
            .comment("determines how many blocks per tick should be broken by the multi-break enchantments")
            .defineInRange("iter_max_broken", 20, 1, 200);
    private static final ForgeConfigSpec.BooleanValue DISABLE_ANVIL_LIMIT = BUILDER
            .comment("determines whether to disable anvil \"Too Expensive\" cap")
            .define("disable_anvil_limit", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

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
