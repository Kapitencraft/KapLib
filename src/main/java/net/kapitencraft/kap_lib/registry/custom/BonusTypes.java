package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.item.bonus.type.AttributeModifiersBonus;
import net.kapitencraft.kap_lib.item.bonus.type.EffectsBonus;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface BonusTypes {

    DeferredRegister<DataPackSerializer<? extends Bonus<?>>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.BONUS_SERIALIZERS);

    RegistryObject<DataPackSerializer<EffectsBonus>> SIMPLE_MOB_EFFECT = REGISTRY.register("simple_mob_effect", ()-> EffectsBonus.SERIALIZER);
    RegistryObject<DataPackSerializer<AttributeModifiersBonus>> ATTRIBUTE_MODIFIERS = REGISTRY.register("attribute_modifiers", () -> AttributeModifiersBonus.SERIALIZER);
}