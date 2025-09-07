package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.enchantments.abstracts.EnchantmentBowEffect;
import net.kapitencraft.kap_lib.enchantments.abstracts.EnchantmentCountEffect;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.UnaryOperator;

public interface ExtraEnchantmentEffectComponents {

    DeferredRegister<DataComponentType<?>> REGISTRY = KapLibMod.registry(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);

    static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return REGISTRY.register(name, () -> operator.apply(DataComponentType.builder()).build());
    }

    DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>>> SHIELD_BLOCK = register("damage_block", (builder) ->
            builder.persistent(TargetedConditionalEffect.codec(EnchantmentEntityEffect.CODEC, LootContextParamSets.ENCHANTED_ENTITY).listOf()));
    DeferredHolder<DataComponentType<?>, DataComponentType<List<EnchantmentEntityEffect>>> BOW_SPAWN = register("projectile_spawn", builder ->
            builder.persistent(EnchantmentEntityEffect.CODEC.listOf()));
    DeferredHolder<DataComponentType<?>, DataComponentType<List<EnchantmentBowEffect>>> BOW_TICK = register("bow_tick", builder ->
            builder.persistent(EnchantmentBowEffect.CODEC.listOf()));
    DeferredHolder<DataComponentType<?>, DataComponentType<EnchantmentCountEffect>> COUNT = register("count", builder ->
            builder.persistent(EnchantmentCountEffect.CODEC));
}
