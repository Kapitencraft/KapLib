package net.kapitencraft.kap_lib.registry.custom.spawn_table;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.kapitencraft.kap_lib.spawn_table.ForgeGsonAdapterFactory;
import net.kapitencraft.kap_lib.spawn_table.functions.*;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;

public interface SpawnEntityFunctions {
    DeferredRegister<SpawnEntityFunctionType> REGISTRY = KapLibMod.registry(ExtraRegistryKeys.FUNCTION_TYPES);

    RegistryObject<SpawnEntityFunctionType> COMMON_PROPERTIES = register("common_properties", new CommonPropertiesFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> MOB_PROPERTIES = register("mob_properties", new MobSpecificPropertiesFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_NAME = register("set_name", new SetNameFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_MOTION = register("set_motion", new SetMotionFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_FACING = register("set_facing", new SetFacingFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_FIRE_DURATION = register("set_fire", new SetFireFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_AIR_SUPPLY = register("set_air_supply", new SetAirSupplyFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_ARMOR = register("set_armor", new SetArmorFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_LEASHED = register("set_leashed", new SetLeashedFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_HEALTH = register("set_health", new SetHealthFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_OWNER = register("set_owner", new SetEntityOwnerFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> SET_EXPERIENCE_VALUE = register("set_experience_value", new SetExperienceValueFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> ADD_EFFECTS = register("add_effects", new AddEffectsFunction.Serializer());
    RegistryObject<SpawnEntityFunctionType> ADD_PASSENGERS = register("add_passengers", new AddPassengersFunction.Serializer());

    static RegistryObject<SpawnEntityFunctionType> register(String name, Serializer<? extends SpawnEntityFunction> serializer) {
        return REGISTRY.register(name, () -> new SpawnEntityFunctionType(serializer));
    }

    static Object createGsonAdapter() {
        return ForgeGsonAdapterFactory.builder(ExtraRegistries.SPAWN_FUNCTION_TYPES, "function", "function", SpawnEntityFunction::getType).build();
    }

    BiFunction<Entity, SpawnContext, Entity> IDENTITY = (p_80760_, p_80761_) -> p_80760_;

    static BiFunction<Entity, SpawnContext, Entity> compose(BiFunction<Entity, SpawnContext, Entity>[] pFunctions) {
        switch (pFunctions.length) {
            case 0:
                return IDENTITY;
            case 1:
                return pFunctions[0];
            case 2:
                BiFunction<Entity, SpawnContext, Entity> bifunction = pFunctions[0];
                BiFunction<Entity, SpawnContext, Entity> bifunction1 = pFunctions[1];
                return (p_80768_, p_80769_) ->
                        bifunction1.apply(bifunction.apply(p_80768_, p_80769_), p_80769_);
            default:
                return (p_80774_, p_80775_) -> {
                    for(BiFunction<Entity, SpawnContext, Entity> bifunction2 : pFunctions) {
                        p_80774_ = bifunction2.apply(p_80774_, p_80775_);
                    }

                    return p_80774_;
                };
        }
    }
}
