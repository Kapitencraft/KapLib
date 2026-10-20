package net.kapitencraft.kap_lib.enchantment;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentBlockBreakEffect;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentBowEffect;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentCountEffect;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.UnaryOperator;

public interface ExtraEnchantmentEffectComponents {

    DeferredRegister<DataComponentType<?>> REGISTRY = LibConstants.registry(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);

    static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return REGISTRY.register(name, () -> operator.apply(DataComponentType.builder()).build());
    }

    DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> SHIELD_BLOCK = register("damage_block", (builder) ->
            builder.persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentEntityEffect>>>> BOW_SPAWN = register("projectile_spawn", builder ->
            builder.persistent(ConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentBowEffect>>>> BOW = register("bow_tick", builder ->
            builder.persistent(ConditionalEffect.codec(EnchantmentBowEffect.CODEC, EnchantmentBowEffect.LOOT_PARAM_SET).listOf()));
    DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentCountEffect>>>> COUNT = register("count", builder ->
            builder.persistent(TargetedConditionalEffect.codec(EnchantmentCountEffect.CODEC, LootContextParamSets.ENCHANTED_DAMAGE).listOf()));
    DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentBlockBreakEffect>>>> BLOCK_BREAK = register("block_break", listBuilder ->
            listBuilder.persistent(ConditionalEffect.codec(EnchantmentBlockBreakEffect.CODEC, EnchantmentBlockBreakEffect.PARAM_SET).listOf()));
}