package net.kapitencraft.kap_lib.enchantment.abstracts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.enchantment.EnchantmentEffectRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.function.Function;

public interface EnchantmentBlockBreakEffect {
    LootContextParamSet PARAM_SET = LootContextParamSet.builder()
            .required(LootContextParams.ATTACKING_ENTITY)
            .required(LootContextParams.BLOCK_STATE)
            .required(LootContextParams.TOOL)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .optional(LootContextParams.BLOCK_ENTITY)
            .build();

    Codec<EnchantmentBlockBreakEffect> CODEC = EnchantmentEffectRegistries.BLOCK_BREAK.byNameCodec().dispatch(EnchantmentBlockBreakEffect::codec, Function.identity());

    /**
     * @param state the block broken
     * @param pos the position where the block was broken
     * @param level the level the block was broken in
     * @param enchLevel the level of the enchantment
     * @return whether the event should be cancelled
     */
    boolean onBreak(BlockState state, BlockPos pos, ServerLevel level, int enchLevel);

    MapCodec<? extends EnchantmentBlockBreakEffect> codec();
}
