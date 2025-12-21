package net.kapitencraft.kap_lib.attribute;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class TimedModifierUtils {

    public static void add(LivingEntity living, ResourceLocation baseLocation, int duration, Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = living.getAttribute(attribute);
        if (instance == null) throw new IllegalStateException("could not find attribute " + attribute.getKey() + " on entity " + BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()));
        int i = 0;
        while (instance.hasModifier(baseLocation.withSuffix("_" + i))) i++;
        ResourceLocation location = baseLocation.withSuffix("_" + i);
        instance.addPermanentModifier(new AttributeModifier(location, amount, operation));
        TimedModifiers modifiers = TimedModifiers.get(living);
        modifiers.add(duration, attribute, location);

    }
}
