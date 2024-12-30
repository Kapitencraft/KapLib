package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.io.serialization.DataGenSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.item.bonus.type.SimpleSetMobEffect;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface SetBonusTypes {

    DeferredRegister<DataGenSerializer<? extends Bonus<?>>> REGISTRY = KapLibMod.registry(ExtraRegistryKeys.SET_BONUSES);

    RegistryObject<DataGenSerializer<SimpleSetMobEffect>> SIMPLE_MOB_EFFECT = REGISTRY.register("simple_mob_effect", ()-> Bonus.createSerializer(SimpleSetMobEffect.CODEC, SimpleSetMobEffect::fromNetwork));
}