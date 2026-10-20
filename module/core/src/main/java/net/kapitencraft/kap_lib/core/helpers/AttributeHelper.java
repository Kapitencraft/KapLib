package net.kapitencraft.kap_lib.core.helpers;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;

public interface AttributeHelper {

    /**
     * simple null-save attribute value method
     */
    static double getSaveAttributeValue(Holder<Attribute> attribute, @Nullable LivingEntity living) {
        if (living != null && living.getAttribute(attribute) != null) {
            return living.getAttributeValue(attribute);
        }
        return 0;
    }

    /**
     * method that replaces AttributeInstance#calculateValue using a custom base Value
     * @param baseValue the base value
     * @return the value of the instance using the base value
     */
    static double getAttributeValue(@Nullable AttributeInstance instance, double baseValue) {
        if (instance == null) {
            return baseValue;
        }
        double d0 = baseValue + instance.getBaseValue();

        for (AttributeModifier attributemodifier : instance.getModifiers(AttributeModifier.Operation.ADD_VALUE).values()) {
            d0 += attributemodifier.amount();
        }

        double d1 = d0;

        for (AttributeModifier attributeModifier1 : instance.getModifiers(AttributeModifier.Operation.ADD_MULTIPLIED_BASE).values()) {
            d1 += d0 * attributeModifier1.amount();
        }

        for (AttributeModifier attributeModifier2 : instance.getModifiers(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).values()) {
            d1 *= 1.0D + attributeModifier2.amount();
        }
        return instance.getAttribute().value().sanitizeValue(d1);
    }
}