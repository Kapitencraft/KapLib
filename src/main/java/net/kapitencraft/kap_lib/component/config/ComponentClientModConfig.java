package net.kapitencraft.kap_lib.component.config;

import net.neoforged.neoforge.common.ModConfigSpec;

//TODO figure out how to do config compatible with KapLib and modules
public class ComponentClientModConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        CACHE_PLAYER_HEADS = BUILDER
                .comment("determines whether the player heads texture will be cached for further use")
                .comment("may help when playing on servers with many and always the same players")
                .define("cache_player_heads", false);
    }

    public static final ModConfigSpec SPEC = BUILDER.build();


    private static final ModConfigSpec.BooleanValue CACHE_PLAYER_HEADS;

    public static boolean cachePlayerHeads() {
        return CACHE_PLAYER_HEADS.get();
    }
}