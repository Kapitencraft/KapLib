package net.kapitencraft.kap_lib.registry.custom.spawn_table;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.spawn_table.entries.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface SpawnPoolEntries {
    Codec<SpawnPoolEntryContainer> CODEC = ExtraRegistries.SPAWN_POOL_ENTRY_TYPES.byNameCodec().dispatch(SpawnPoolEntryContainer::getType, SpawnPoolEntryType::codec);

    DeferredRegister<SpawnPoolEntryType> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.POOL_ENTRY_TYPES);

    Supplier<SpawnPoolEntryType> ALTERNATIVES = register("alternatives", AlternativesEntry.CODEC);
    Supplier<SpawnPoolEntryType> GROUP = register("group", EntryGroup.CODEC);
    Supplier<SpawnPoolEntryType> SEQUENCE = register("sequence", SequentialEntry.CODEC);
    Supplier<SpawnPoolEntryType> DYNAMIC = register("dynamic", DynamicSpawn.CODEC);
    Supplier<SpawnPoolEntryType> EMPTY = register("empty", EmptySpawnEntity.CODEC);
    Supplier<SpawnPoolEntryType> ENTITY = register("entity", SpawnEntity.CODEC);
    Supplier<SpawnPoolEntryType> REFERENCE = register("reference", NestedSpawnTable.CODEC);
    Supplier<SpawnPoolEntryType> EFFECT_CLOUD = register("effect_cloud", SpawnEffectCloud.CODEC);

    static Supplier<SpawnPoolEntryType> register(String name, MapCodec<? extends SpawnPoolEntryContainer> serializer) {
        return REGISTRY.register(name, () -> new SpawnPoolEntryType(serializer));
    }
}
