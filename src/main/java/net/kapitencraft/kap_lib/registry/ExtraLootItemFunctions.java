package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.kapitencraft.kap_lib.item.loot_table.functions.AttributeAmountModifierFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ExtraLootItemFunctions {
    DeferredRegister<LootItemFunctionType<?>> REGISTRY = KapLibMod.registry(Registries.LOOT_FUNCTION_TYPE);

    Holder<LootItemFunctionType<?>> ATTRIBUTE_MODIFIER = REGISTRY.register("attribute_modifier", type(AttributeAmountModifierFunction.SERIALIZER));

    private static Supplier<LootItemFunctionType> type(MapCodec<? extends LootItemFunction> serializer) {
        return ()-> new LootItemFunctionType(serializer);
    }
}
