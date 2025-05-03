package net.kapitencraft.kap_lib.mixin.classes;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.io.serialization.NbtSerializer;
import net.kapitencraft.kap_lib.mixin.duck.attribute.IKapLibAttributeModifier;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaAttributeModifierTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AttributeModifier.class)
public class AttributeModifierMixin implements IKapLibAttributeModifier {
    /**
     * can't use static fields due to MobEffects preloading Attribute Modifiers
     */
    @Unique
    private static NbtSerializer<AttributeModifier> createSerializer() {
        return new NbtSerializer<>(ExtraCodecs.ATTRIBUTE_MODIFIER);
    }

    @Override
    public Codec<? extends AttributeModifier> getCodec() {
        return VanillaAttributeModifierTypes.DEFAULT.get();
    }

    @Override
    public boolean tickBased() {
        return false;
    }

    @Override
    public boolean tick() {
        return false;
    }

    private AttributeModifier self() {
        return (AttributeModifier) (Object) this;
    }

    /**
     * @author Kapitencraft
     * @reason custom registry for Attribute modifiers
     */
    @Overwrite
    public CompoundTag save() {
        return (CompoundTag) createSerializer().encode(self());
    }

    /**
     * @author Kapitencraft
     * @reason custom registry for Attribute modifiers
     */
    @Overwrite
    public static AttributeModifier load(CompoundTag pNbt) {
        return createSerializer().parse(pNbt);
    }
}
