package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.item.bonus.type.EffectsBonus;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface SetBonusTypes {

    DeferredRegister<DataPackSerializer<? extends Bonus<?>>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.SET_BONUSES);

    RegistryObject<DataPackSerializer<EffectsBonus>> SIMPLE_MOB_EFFECT = REGISTRY.register("simple_mob_effect", ()-> Bonus.createSerializer(EffectsBonus.CODEC, EffectsBonus::fromNetwork));
}