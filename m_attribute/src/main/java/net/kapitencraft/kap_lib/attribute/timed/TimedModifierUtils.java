package net.kapitencraft.kap_lib.attribute.timed;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * utilities to create timed modifiers
 */
public class TimedModifierUtils {

    /**
     * adds a new timed modifier to the given entity. timed modifiers expire after the given duration
     * @param living the entity to add the modifier to
     * @param baseLocation the base modifier location
     * @param duration the duration the modifier should exist for
     * @param attribute the attribute the modifier is applied to
     * @param amount the amount of the modifier
     * @param operation the operation of the modifier
     */
    public static void add(LivingEntity living, ResourceLocation baseLocation, int duration, Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = living.getAttribute(attribute);
        if (instance == null) throw new IllegalStateException("could not find attribute " + attribute.getKey() + " on entity " + BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()));
        int i = 0;
        while (instance.hasModifier(baseLocation.withSuffix("_" + i))) i++; //check if other timed modifiers with that name already exist
        ResourceLocation location = baseLocation.withSuffix("_" + i);
        instance.addPermanentModifier(new AttributeModifier(location, amount, operation));
        TimedModifiers modifiers = TimedModifiers.get(living);
        modifiers.add(duration, attribute, location);

    }
}
