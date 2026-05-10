package net.kapitencraft.kap_lib.attribute.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Attributes.class)
class AttributesMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "NEW", target = "(Ljava/lang/String;DDD)Lnet/minecraft/world/entity/ai/attributes/RangedAttribute;"))
    private static RangedAttribute change(String descriptionId, double defaultValue, double min, double max, Operation<RangedAttribute> original) {
        RangedAttribute attribute;
        //change max value of max_health and armor to max value
        if ("attribute.name.generic.max_health".equals(descriptionId) || "attribute.name.generic.armor".equals(descriptionId))
            attribute = original.call(descriptionId, defaultValue, min, Double.MAX_VALUE);
        else
            attribute = original.call(descriptionId, defaultValue, min, max);
        //add synchronization to the attack damage attribute
        if ("attribute.name.generic.attack_damage".equals(descriptionId))
            attribute = (RangedAttribute) attribute.setSyncable(true);
        return attribute;
    }

}
