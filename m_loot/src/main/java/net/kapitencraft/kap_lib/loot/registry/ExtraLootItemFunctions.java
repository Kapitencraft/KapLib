package net.kapitencraft.kap_lib.loot.registry;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.loot.functions.AttributeAmountModifierFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ExtraLootItemFunctions {
    DeferredRegister<LootItemFunctionType<?>> REGISTRY = LibConstants.registry(Registries.LOOT_FUNCTION_TYPE);

    Supplier<LootItemFunctionType<AttributeAmountModifierFunction>> ATTRIBUTE_MODIFIER = REGISTRY.register("attribute_modifier", type(AttributeAmountModifierFunction.CODEC));

    private static <T extends LootItemFunction> Supplier<LootItemFunctionType<T>> type(MapCodec<T> serializer) {
        return ()-> new LootItemFunctionType<>(serializer);
    }
}
