package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.AddItemModifier;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.EnchantmentAddItemModifier;
import net.kapitencraft.kap_lib.item.loot_table.modifiers.OreModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ExtraLootModifiers {

    DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = KapLibMod.registry(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS);

    RegistryObject<Codec<AddItemModifier>> ADD_ITEM = REGISTRY.register("add_item", ()-> AddItemModifier.CODEC);
    RegistryObject<Codec<EnchantmentAddItemModifier>> ENCH_ADD_ITEM = REGISTRY.register("ench_add_item", ()-> EnchantmentAddItemModifier.CODEC);
    RegistryObject<Codec<OreModifier>> ORE = REGISTRY.register("ore_mod", ()-> OreModifier.CODEC);
}
