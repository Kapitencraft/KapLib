package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.AddItemModifier;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.EnchantmentAddItemModifier;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.OreModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public interface ExtraLootModifiers {

    DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTRY = KapLibMod.registry(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS);

    Supplier<MapCodec<AddItemModifier>> ADD_ITEM = REGISTRY.register("add_item", ()-> AddItemModifier.CODEC);
    Supplier<MapCodec<EnchantmentAddItemModifier>> ENCH_ADD_ITEM = REGISTRY.register("ench_add_item", ()-> EnchantmentAddItemModifier.CODEC);
    Supplier<MapCodec<OreModifier>> ORE = REGISTRY.register("ore_mod", ()-> OreModifier.CODEC);
}
