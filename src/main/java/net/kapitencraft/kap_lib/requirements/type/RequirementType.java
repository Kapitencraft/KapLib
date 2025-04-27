package net.kapitencraft.kap_lib.requirements.type;

import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

public interface RequirementType<T> {
    RequirementType<Enchantment> ENCHANTMENT = new RegistryReqType<>("enchantment", ForgeRegistries.ENCHANTMENTS);
    RequirementType<Item> ITEM = new RegistryReqType<>("item", ForgeRegistries.ITEMS);
    RequirementType<BonusManager.BonusElement> BONUS = new BonusRequirementType();


    ResourceLocation getId(T value);

    T getById(ResourceLocation location);

    String getName();
}
