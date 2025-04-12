package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Consumer;

/**
 * A loot pool entry that does not generate any items.
 */
public class EmptySpawnEntity extends SpawnPoolSingletonContainer {
   EmptySpawnEntity(int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions) {
      super(pWeight, pQuality, pConditions, pFunctions);
   }

   public SpawnPoolEntryType getType() {
      return SpawnPoolEntries.EMPTY.get();
   }

   /**
    * empty. does not generate anything (what did you expect?)
    */
   public void createEntity(Consumer<Entity> pEntityConsumer, SpawnContext pLootContext) {
   }

   public static Builder<?> emptyItem() {
      return simpleBuilder(EmptySpawnEntity::new);
   }

   public static class Serializer extends SpawnPoolSingletonContainer.Serializer<EmptySpawnEntity> {
      public EmptySpawnEntity deserialize(JsonObject pObject, JsonDeserializationContext pContext, int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions) {
         return new EmptySpawnEntity(pWeight, pQuality, pConditions, pFunctions);
      }
   }
}