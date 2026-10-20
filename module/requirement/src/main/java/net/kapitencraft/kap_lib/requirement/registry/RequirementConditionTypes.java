package net.kapitencraft.kap_lib.requirement.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.requirement.conditions.CustomStatReqCondition;
import net.kapitencraft.kap_lib.requirement.conditions.DimensionReqCondition;
import net.kapitencraft.kap_lib.requirement.conditions.StatReqCondition;
import net.kapitencraft.kap_lib.requirement.conditions.abstracts.ReqCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public interface RequirementConditionTypes {

    DeferredRegister<RegistrySerializer<? extends ReqCondition<?>>> REGISTRY = LibConstants.registry(RequirementRegistries.Keys.REQ_CONDITIONS);

    Supplier<RegistrySerializer<StatReqCondition>> STAT = REGISTRY.register("stat", () -> StatReqCondition.SERIALIZER);
    Supplier<RegistrySerializer<CustomStatReqCondition>> CUSTOM_STAT = REGISTRY.register("custom_stat", () -> CustomStatReqCondition.SERIALIZER);
    Supplier<RegistrySerializer<DimensionReqCondition>> DIMENSION = REGISTRY.register("dimension", () -> DimensionReqCondition.SERIALIZER);
}
