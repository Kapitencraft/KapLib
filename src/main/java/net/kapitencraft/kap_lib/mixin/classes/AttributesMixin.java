package net.kapitencraft.kap_lib.mixin.classes;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Attributes.class)
public class AttributesMixin {

    @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "(Ljava/lang/String;DDD)Lnet/minecraft/world/entity/ai/attributes/RangedAttribute;"))
    private static RangedAttribute change(String pDescriptionId, double pDefaultValue, double pMin, double pMax) {
        RangedAttribute attribute;
        //change max value of max_health and armor to max value
        if ("attribute.name.generic.max_health".equals(pDescriptionId) || "attribute.name.generic.armor".equals(pDescriptionId))
            attribute = new RangedAttribute(pDescriptionId, pDefaultValue, pMin, Double.MAX_VALUE);
        else
            attribute = new RangedAttribute(pDescriptionId, pDefaultValue, pMin, pMax);
        //add synchronization to the attack damage attribute
        if ("attribute.name.generic.attack_damage".equals(pDescriptionId))
            attribute = (RangedAttribute) attribute.setSyncable(true);
        return attribute;
    }

}
