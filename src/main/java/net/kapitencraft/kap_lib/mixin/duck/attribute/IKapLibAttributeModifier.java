package net.kapitencraft.kap_lib.mixin.duck.attribute;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface IKapLibAttributeModifier {

    static IKapLibAttributeModifier of(AttributeModifier pModifier) {
        return ((IKapLibAttributeModifier) pModifier);
    }

    Codec<? extends AttributeModifier> getCodec();

    boolean tickBased();

    /**
     * @return whether to remove this modifier or not
     */
    boolean tick();

    static Codec<AttributeModifier> codecFromVanilla(AttributeModifier attributeModifier) {
        return (Codec<AttributeModifier>) of(attributeModifier).getCodec();
    }
}
