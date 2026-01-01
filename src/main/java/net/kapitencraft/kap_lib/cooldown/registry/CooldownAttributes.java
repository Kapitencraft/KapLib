package net.kapitencraft.kap_lib.cooldown.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public interface CooldownAttributes {
    ResourceLocation BASE_COOLDOWN_REDUCTION_LOC = LibConstants.res("base_cooldown_reduction");

    DeferredRegister<Attribute> REGISTRY = LibConstants.registry(Registries.ATTRIBUTE);

    Holder<Attribute> COOLDOWN_REDUCTION = registerNegative("cooldown_reduction", 0, 0, 100, BASE_COOLDOWN_REDUCTION_LOC);

    private static Holder<Attribute> registerNegative(String name, double initValue, double minValue, double maxValue, ResourceLocation baseId) {
        return REGISTRY.register("generic." + name, () -> new RangedAttribute("generic." + name, initValue, minValue, maxValue) {
            @Override
            public @Nullable ResourceLocation getBaseId() {
                return baseId;
            }
        }.setSentiment(Attribute.Sentiment.NEGATIVE).setSyncable(true));
    }
}
