package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.AddItemModifier;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.EnchantmentAddItemModifier;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.OreModifier;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public interface ExtraLootModifiers {

    DeferredRegister<MapCodec<? extends IGlobalLootModifier>> REGISTRY = KapLibMod.registry(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS);

    Holder<MapCodec<AddItemModifier>> ADD_ITEM = REGISTRY.register("add_item", ()-> AddItemModifier.CODEC);
    Holder<Codec<EnchantmentAddItemModifier>> ENCH_ADD_ITEM = REGISTRY.register("ench_add_item", ()-> EnchantmentAddItemModifier.CODEC);
    Holder<Codec<OreModifier>> ORE = REGISTRY.register("ore_mod", ()-> OreModifier.CODEC);
}
