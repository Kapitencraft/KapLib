package net.kapitencraft.kap_lib.requirement.type;

import net.kapitencraft.kap_lib.core.io.serialization.DataPackSerializer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

@MethodsReturnNonnullByDefault
public interface RequirementType<T> {
    RegistryReqType<Item> ITEM = RegistryReqType.registry("item", BuiltInRegistries.ITEM, Registries.ITEM);
    RegistryReqType<Block> ENCHANTMENT = RegistryReqType.registry("block", BuiltInRegistries.BLOCK, Registries.BLOCK);

    DataPackSerializer<T> serializer();

    String getName();
}
