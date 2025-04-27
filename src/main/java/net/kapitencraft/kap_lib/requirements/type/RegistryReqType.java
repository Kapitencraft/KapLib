package net.kapitencraft.kap_lib.requirements.type;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class RegistryReqType<T> implements RequirementType<T> {
    public static final RegistryReqType<Item> ITEM = new RegistryReqType<>("item", ForgeRegistries.ITEMS);
    public static final RegistryReqType<Enchantment> ENCHANTMENT = new RegistryReqType<>("enchantment", ForgeRegistries.ENCHANTMENTS);

    private final String name;
    private final IForgeRegistry<T> registry;

    public RegistryReqType(String name, IForgeRegistry<T> registry) {
        this.name = name;
        this.registry = registry;
    }

    public ResourceLocation getId(T value) {
        return this.registry.getKey(value);
    }

    public T getById(ResourceLocation location) {
        return this.registry.getValue(location);
    }

    public String getName() {
        return name;
    }
}