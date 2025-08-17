package net.kapitencraft.kap_lib.mixin.duck.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface IKapLibAttributeModifier {

    static IKapLibAttributeModifier of(AttributeModifier pModifier) {
        return ((IKapLibAttributeModifier) pModifier);
    }

    MapCodec<? extends AttributeModifier> getCodec();

    boolean tickBased();

    /**
     * @return whether to remove this modifier or not
     */
    boolean tick();

    static Codec<AttributeModifier> codecFromVanilla(AttributeModifier attributeModifier) {
        return (Codec<AttributeModifier>) of(attributeModifier).getCodec();
    }
}
