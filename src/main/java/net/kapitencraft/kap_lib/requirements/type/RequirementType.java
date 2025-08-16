package net.kapitencraft.kap_lib.requirements.type;

import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.AbstractBonusElement;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

@MethodsReturnNonnullByDefault
public interface RequirementType<T> {
    RegistryReqType<Item> ITEM = RegistryReqType.registry("item", BuiltInRegistries.ITEM, Registries.ITEM);
    RegistryHolderReqType<Enchantment> ENCHANTMENT = RegistryReqType.registryHolder("enchantment", Registries.ENCHANTMENT);
    RequirementType<AbstractBonusElement> BONUS = new BonusRequirementType();

    DataPackSerializer<T> serializer();

    String getName();
}
