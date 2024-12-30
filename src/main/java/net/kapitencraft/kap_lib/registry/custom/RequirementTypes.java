package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.io.serialization.DataGenSerializer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.kapitencraft.kap_lib.requirements.type.DimensionReqCondition;
import net.kapitencraft.kap_lib.requirements.type.StatReqCondition;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface RequirementTypes {

    DeferredRegister<DataGenSerializer<? extends ReqCondition<?>>> REGISTRY = KapLibMod.registry(ExtraRegistryKeys.REQ_CONDITIONS);

    RegistryObject<DataGenSerializer<StatReqCondition>> STAT_REQ = REGISTRY.register("stat_req", () -> ReqCondition.createSerializer(StatReqCondition.CODEC, StatReqCondition::fromNetwork));
    RegistryObject<DataGenSerializer<DimensionReqCondition>> DIMENSION = REGISTRY.register("dimension", () -> ReqCondition.createSerializer(DimensionReqCondition.CODEC, DimensionReqCondition::fromNetwork));
}
