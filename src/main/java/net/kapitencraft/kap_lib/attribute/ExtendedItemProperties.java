package net.kapitencraft.kap_lib.attribute;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;

public class ExtendedItemProperties {

    public static final ClampedItemPropertyFunction BOW_PULL = (stack, level, living, p_174679_) -> {
        if (living == null || living.getAttribute(ExtraAttributes.DRAW_SPEED) == null) {
            return 0.0F;
        } else {
            return living.getUseItem() != stack ? 0.0F : (float) ((stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / 20.0F * living.getAttributeValue(ExtraAttributes.DRAW_SPEED) / 100);
        }
    };
}
