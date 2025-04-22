package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.requirements.type.DimensionReqCondition;
import net.kapitencraft.kap_lib.requirements.type.StatReqCondition;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface RequirementTypes {

    DeferredRegister<DataPackSerializer<? extends ReqCondition<?>>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.REQ_CONDITIONS);

    RegistryObject<DataPackSerializer<StatReqCondition>> STAT_REQ = REGISTRY.register("stat_req", () -> StatReqCondition.SERIALIZER);
    RegistryObject<DataPackSerializer<DimensionReqCondition>> DIMENSION = REGISTRY.register("dimension", () -> DimensionReqCondition.SERIALIZER);
}
