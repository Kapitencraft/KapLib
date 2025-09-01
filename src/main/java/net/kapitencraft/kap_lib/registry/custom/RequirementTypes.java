package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.requirements.conditions.CustomStatReqCondition;
import net.kapitencraft.kap_lib.requirements.conditions.DimensionReqCondition;
import net.kapitencraft.kap_lib.requirements.conditions.StatReqCondition;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public interface RequirementTypes {

    DeferredRegister<RegistrySerializer<? extends ReqCondition<?>>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.REQ_CONDITIONS);

    Supplier<RegistrySerializer<StatReqCondition>> STAT = REGISTRY.register("stat", () -> StatReqCondition.SERIALIZER);
    Supplier<RegistrySerializer<CustomStatReqCondition>> CUSTOM_STAT = REGISTRY.register("custom_stat", () -> CustomStatReqCondition.SERIALIZER);
    Supplier<RegistrySerializer<DimensionReqCondition>> DIMENSION = REGISTRY.register("dimension", () -> DimensionReqCondition.SERIALIZER);
}
