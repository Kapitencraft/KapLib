package net.kapitencraft.kap_lib.bonus.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.bonus.BonusEffect;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.bonus.type.AttributeModifiersBonusEffect;
import net.kapitencraft.kap_lib.bonus.type.EffectsBonusEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface BonusTypes {

    DeferredRegister<RegistrySerializer<? extends BonusEffect>> REGISTRY = LibConstants.registry(BonusRegistries.Keys.EFFECT_SERIALIZERS);

    Supplier<RegistrySerializer<EffectsBonusEffect>> SIMPLE_MOB_EFFECT = REGISTRY.register("simple_mob_effect", () -> EffectsBonusEffect.SERIALIZER);
    Supplier<RegistrySerializer<AttributeModifiersBonusEffect>> ATTRIBUTE_MODIFIERS = REGISTRY.register("attribute_modifiers", () -> AttributeModifiersBonusEffect.SERIALIZER);
}