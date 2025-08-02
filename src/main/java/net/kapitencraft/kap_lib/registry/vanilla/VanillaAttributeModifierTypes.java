package net.kapitencraft.kap_lib.registry.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.ExtraCodecs;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface VanillaAttributeModifierTypes {
    static Codec<AttributeModifier> createVanillaCodec() {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        UUIDUtil.STRING_CODEC.fieldOf("uuid").forGetter(AttributeModifier::getId),
                        Codec.STRING.fieldOf("name").forGetter(AttributeModifier::getName),
                        Codec.DOUBLE.fieldOf("amount").forGetter(AttributeModifier::getAmount),
                        OPERATION_CODEC.fieldOf("operation").forGetter(AttributeModifier::getOperation)
                ).apply(instance, AttributeModifier::new)
        );
    }

    Codec<AttributeModifier.Operation> OPERATION_CODEC = Codec.INT.xmap(AttributeModifier.Operation::fromValue, AttributeModifier.Operation::toValue);

    DeferredRegister<Codec<? extends AttributeModifier>> REGISTRY = DeferredRegister.create(ExtraRegistries.Keys.ATTRIBUTE_MODIFIER_TYPES, "minecraft");

    RegistryObject<Codec<AttributeModifier>> DEFAULT = REGISTRY.register("default", VanillaAttributeModifierTypes::createVanillaCodec);
}
