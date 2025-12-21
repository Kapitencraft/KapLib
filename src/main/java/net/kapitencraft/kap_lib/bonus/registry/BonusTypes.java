package net.kapitencraft.kap_lib.bonus.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.bonus.Bonus;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.bonus.type.AttributeModifiersBonus;
import net.kapitencraft.kap_lib.bonus.type.EffectsBonus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface BonusTypes {

    DeferredRegister<RegistrySerializer<? extends Bonus<?>>> REGISTRY = LibConstants.registry(BonusRegistries.Keys.SERIALIZERS);

    Supplier<RegistrySerializer<EffectsBonus>> SIMPLE_MOB_EFFECT = REGISTRY.register("simple_mob_effect", () -> EffectsBonus.SERIALIZER);
    Supplier<RegistrySerializer<AttributeModifiersBonus>> ATTRIBUTE_MODIFIERS = REGISTRY.register("attribute_modifiers", () -> AttributeModifiersBonus.SERIALIZER);
}