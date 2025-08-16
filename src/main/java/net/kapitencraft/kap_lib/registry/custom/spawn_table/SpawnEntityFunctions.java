package net.kapitencraft.kap_lib.registry.custom.spawn_table;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.spawn_table.functions.*;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.commons.compress.archivers.zip.ScatterZipOutputStream;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface SpawnEntityFunctions {
    Codec<SpawnEntityFunction> TYPED_CODEC = ExtraRegistries.SPAWN_FUNCTION_TYPES
            .byNameCodec()
            .dispatch("function", SpawnEntityFunction::getType, SpawnEntityFunctionType::codec);

    Codec<SpawnEntityFunction> ROOT_CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(TYPED_CODEC, SequenceFunction.INLINE_CODEC));

    DeferredRegister<SpawnEntityFunctionType<?>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.FUNCTION_TYPES);

    Supplier<SpawnEntityFunctionType<CommonPropertiesFunction>> COMMON_PROPERTIES = register("common_properties", CommonPropertiesFunction.CODEC);
    Supplier<SpawnEntityFunctionType<MobPropertiesFunction>> MOB_PROPERTIES = register("mob_properties", MobPropertiesFunction.CODEC);
    Supplier<SpawnEntityFunctionType<RaiderPropertiesFunction>> RAIDER_PROPERTIES = register("raider_properties", RaiderPropertiesFunction.CODEC);
    Supplier<SpawnEntityFunctionType<VillagerPropertiesFunction>> VILLAGER_PROPERTIES = register("villager_properties", VillagerPropertiesFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetNameFunction>> SET_NAME = register("set_name", SetNameFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetMotionFunction>> SET_MOTION = register("set_motion", SetMotionFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetFacingFunction>> SET_FACING = register("set_facing", SetFacingFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetFireFunction>> SET_FIRE_DURATION = register("set_fire", SetFireFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetAirSupplyFunction>> SET_AIR_SUPPLY = register("set_air_supply", SetAirSupplyFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetArmorFunction>> SET_ARMOR = register("set_armor", SetArmorFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetLeashedFunction>> SET_LEASHED = register("set_leashed", SetLeashedFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetHealthFunction>> SET_HEALTH = register("set_health", SetHealthFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetEntityOwnerFunction>> SET_OWNER = register("set_owner", SetEntityOwnerFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetExperienceValueFunction>> SET_EXPERIENCE_VALUE = register("set_experience_value", SetExperienceValueFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetAttributesFunction>> SET_ATTRIBUTES = register("set_attributes", SetAttributesFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SetMerchantTradesFunction>> SET_MERCHANT_TRADES = register("set_merchant_trades", SetMerchantTradesFunction.CODEC);
    Supplier<SpawnEntityFunctionType<AddEffectsFunction>> ADD_EFFECTS = register("add_effects", AddEffectsFunction.CODEC);
    Supplier<SpawnEntityFunctionType<AddPassengersFunction>> ADD_PASSENGERS = register("add_passengers", AddPassengersFunction.CODEC);
    Supplier<SpawnEntityFunctionType<SequenceFunction>> SEQUENCE = register("sequence", SequenceFunction.CODEC);

    static <T extends SpawnEntityFunction> DeferredHolder<SpawnEntityFunctionType<?>, SpawnEntityFunctionType<T>> register(String name, MapCodec<T> serializer) {
        return REGISTRY.register(name, () -> new SpawnEntityFunctionType<>(serializer));
    }

    BiFunction<Entity, SpawnContext, Entity> IDENTITY = (p_80760_, p_80761_) -> p_80760_;

    static BiFunction<Entity, SpawnContext, Entity> compose(List<? extends BiFunction<Entity, SpawnContext, Entity>> pFunctions) {
        switch (pFunctions.size()) {
            case 0:
                return IDENTITY;
            case 1:
                return pFunctions.getFirst();
            case 2:
                BiFunction<Entity, SpawnContext, Entity> bifunction = pFunctions.get(0);
                BiFunction<Entity, SpawnContext, Entity> bifunction1 = pFunctions.get(1);
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
