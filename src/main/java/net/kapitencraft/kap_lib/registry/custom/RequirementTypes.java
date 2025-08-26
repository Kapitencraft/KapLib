package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.requirements.conditions.CustomStatReqCondition;
import net.kapitencraft.kap_lib.requirements.conditions.DimensionReqCondition;
import net.kapitencraft.kap_lib.requirements.conditions.StatReqCondition;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface RequirementTypes {

    DeferredRegister<DataPackSerializer<? extends ReqCondition<?>>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.REQ_CONDITIONS);

    RegistryObject<DataPackSerializer<StatReqCondition>> STAT = REGISTRY.register("stat", () -> StatReqCondition.SERIALIZER);
    RegistryObject<DataPackSerializer<CustomStatReqCondition>> CUSTOM_STAT = REGISTRY.register("custom_stat", () -> CustomStatReqCondition.SERIALIZER);
    RegistryObject<DataPackSerializer<DimensionReqCondition>> DIMENSION = REGISTRY.register("dimension", () -> DimensionReqCondition.SERIALIZER);
}
