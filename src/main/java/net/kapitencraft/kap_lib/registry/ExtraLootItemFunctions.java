package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.item.loot_table.functions.AttributeAmountModifierFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public interface ExtraLootItemFunctions {
    DeferredRegister<LootItemFunctionType> REGISTRY = KapLibMod.registry(Registries.LOOT_FUNCTION_TYPE);

    RegistryObject<LootItemFunctionType> ATTRIBUTE_MODIFIER = REGISTRY.register("attribute_modifier", type(AttributeAmountModifierFunction.SERIALIZER));

    private static Supplier<LootItemFunctionType> type(Serializer<? extends LootItemFunction> serializer) {
        return ()-> new LootItemFunctionType(serializer);
    }
}
