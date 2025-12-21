package net.kapitencraft.kap_lib.mana;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.attribute.BaseAttributeLocations;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public interface ManaAttributes {
    ResourceLocation BASE_MANA_COST_LOC = LibConstants.res("base_mana_cost");

    DeferredRegister<Attribute> REGISTRY = LibConstants.registry(Registries.ATTRIBUTE);

    private static Holder<Attribute> register(String name, double initValue, double minValue, double maxValue, @Nullable ResourceLocation baseLocation) {
        return REGISTRY.register("generic." + name, ()-> new RangedAttribute("generic." + name, initValue, minValue, maxValue) {
            @Override
            public @Nullable ResourceLocation getBaseId() {
                return baseLocation;
            }
        }.setSyncable(true));
    }

    private static Holder<Attribute> registerNegative(String name, double initValue, double minValue, double maxValue, ResourceLocation baseId) {
        return REGISTRY.register("generic." + name, () -> new RangedAttribute("generic." + name, initValue, minValue, maxValue) {
            @Override
            public @Nullable ResourceLocation getBaseId() {
                return baseId;
            }
        }.setSentiment(Attribute.Sentiment.NEGATIVE).setSyncable(true));
    }

    private static Holder<Attribute> register0Max(String name, double initValue, ResourceLocation baseLocation) {
        return register(name, initValue, 0, Double.MAX_VALUE, baseLocation);
    }

    //Mana
    Holder<Attribute> MAX_MANA = register0Max("max_mana", 100, null);
    Holder<Attribute> MANA_COST = registerNegative("mana_cost", 0, 0, 100000, BASE_MANA_COST_LOC);
    Holder<Attribute> MANA_REGEN = register0Max("mana_regen", 1, null);

}
