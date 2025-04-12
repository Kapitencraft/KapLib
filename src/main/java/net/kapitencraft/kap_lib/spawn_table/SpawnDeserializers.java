package net.kapitencraft.kap_lib.spawn_table;

import com.google.gson.GsonBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnPoolEntryContainer;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;

public class SpawnDeserializers {

   /**
    * Create a GsonBuilder that can deserialize {@link LootItemFunction}.
    */
   public static GsonBuilder createFunctionSerializer() {
      return Deserializers.createConditionSerializer()
              .registerTypeHierarchyAdapter(SpawnPoolEntryContainer.class, SpawnPoolEntries.createGsonAdapter())
              .registerTypeHierarchyAdapter(SpawnEntityFunction.class, SpawnEntityFunctions.createGsonAdapter())
              .registerTypeHierarchyAdapter(NbtProvider.class, NbtProviders.createGsonAdapter());
   }

   /**
    * Create a GsonBuilder that can deserialize {@link LootTable}.
    */
   public static GsonBuilder createSpawnTableSerializer() {
      return createFunctionSerializer()
              .registerTypeAdapter(SpawnPool.class, new SpawnPool.Serializer())
              .registerTypeAdapter(SpawnTable.class, new SpawnTable.Serializer());
   }
}