package net.kapitencraft.kap_lib.registry.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface VanillaAttributeModifierTypes {
    static Codec<AttributeModifier> createVanillaCodec() {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        IOHelper.UUID_CODEC.fieldOf("UUID").forGetter(AttributeModifier::getId),
                        Codec.STRING.fieldOf("Name").forGetter(AttributeModifier::getName),
                        Codec.DOUBLE.fieldOf("Amount").forGetter(AttributeModifier::getAmount),
                        OPERATION_CODEC.fieldOf("Operation").forGetter(AttributeModifier::getOperation)
                ).apply(instance, AttributeModifier::new)
        );
    }

    Codec<AttributeModifier.Operation> OPERATION_CODEC = Codec.INT.xmap(AttributeModifier.Operation::fromValue, AttributeModifier.Operation::toValue);

    DeferredRegister<Codec<? extends AttributeModifier>> REGISTRY = DeferredRegister.create(ExtraRegistryKeys.ATTRIBUTE_MODIFIER_TYPES, "minecraft");

    RegistryObject<Codec<AttributeModifier>> DEFAULT = REGISTRY.register("default", VanillaAttributeModifierTypes::createVanillaCodec);
}
