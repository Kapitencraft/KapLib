package net.kapitencraft.kap_lib.registry.custom.spawn_table;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.spawn_table.ForgeGsonAdapterFactory;
import net.kapitencraft.kap_lib.spawn_table.entries.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface SpawnPoolEntries {
    DeferredRegister<SpawnPoolEntryType> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.POOL_ENTRY_TYPES);

    RegistryObject<SpawnPoolEntryType> ALTERNATIVES = register("alternatives", CompositeEntryBase.createSerializer(AlternativesEntry::new));
    RegistryObject<SpawnPoolEntryType> GROUP = register("group", CompositeEntryBase.createSerializer(EntryGroup::new));
    RegistryObject<SpawnPoolEntryType> SEQUENCE = register("sequence", CompositeEntryBase.createSerializer(SequentialEntry::new));
    RegistryObject<SpawnPoolEntryType> DYNAMIC = register("dynamic", new DynamicSpawn.Serializer());
    RegistryObject<SpawnPoolEntryType> EMPTY = register("empty", new EmptySpawnEntity.Serializer());
    RegistryObject<SpawnPoolEntryType> ENTITY = register("entity", new SpawnEntity.Serializer());
    RegistryObject<SpawnPoolEntryType> REFERENCE = register("reference", new SpawnTableReference.Serializer());
    RegistryObject<SpawnPoolEntryType> EFFECT_CLOUD = register("effect_cloud", new SpawnEffectCloud.Serializer());

    static RegistryObject<SpawnPoolEntryType> register(String name, SpawnPoolEntryContainer.Serializer<? extends SpawnPoolEntryContainer> serializer) {
        return REGISTRY.register(name, () -> new SpawnPoolEntryType(serializer));
    }

    static Object createGsonAdapter() {
        return ForgeGsonAdapterFactory.builder(ExtraRegistries.SPAWN_POOL_ENTRY_TYPES, "entry", "type", SpawnPoolEntryContainer::getType).build();
    }

}
