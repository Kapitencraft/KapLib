package net.kapitencraft.kap_lib.multiblock;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MultiblockModuleServerConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        ASYNC_PATTERN_MATCHING_VOLUME_THRESHOLD = BUILDER.comment(
                "determines the minimum volume in a search for a structure pattern match before using asynchronous matching to find the result",
                "note that this option only accelerates the matching process for hardware with multiple CPUs and many different structures."
        ).defineInRange("async_matching_volume_threshold", 100000, 1000, 1000000000);
    }

    private static final ModConfigSpec.IntValue ASYNC_PATTERN_MATCHING_VOLUME_THRESHOLD;

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int getAsyncPatternMatchingThreshold() {
        return ASYNC_PATTERN_MATCHING_VOLUME_THRESHOLD.getAsInt();
    }
}
