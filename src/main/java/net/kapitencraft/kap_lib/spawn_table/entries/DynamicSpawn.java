package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Consumer;

/**
 * A loot pool entry container that will generate the dynamic drops with a given name.
 * 
 */
public class DynamicSpawn extends SpawnPoolSingletonContainer {
   final ResourceLocation name;

   DynamicSpawn(ResourceLocation pDynamicDropsName, int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions) {
      super(pWeight, pQuality, pConditions, pFunctions);
      this.name = pDynamicDropsName;
   }

   public SpawnPoolEntryType getType() {
      return SpawnPoolEntries.DYNAMIC.get();
   }

   /**
    * Generate the loot stacks of this entry.
    * Contrary to the method name this method does not always generate one stack, it can also generate zero or multiple
    * stacks.
    */
   public void createEntity(Consumer<Entity> pEntityConsumer, SpawnContext pLootContext) {
      pLootContext.addDynamicSpawn(this.name, pEntityConsumer);
   }

   public static Builder<?> dynamicEntry(ResourceLocation pDynamicDropsName) {
      return simpleBuilder((p_79487_, p_79488_, p_79489_, p_79490_) ->
              new DynamicSpawn(pDynamicDropsName, p_79487_, p_79488_, p_79489_, p_79490_));
   }

   public static class Serializer extends SpawnPoolSingletonContainer.Serializer<DynamicSpawn> {
      public void serializeCustom(JsonObject pObject, DynamicSpawn pContainer, JsonSerializationContext pConditions) {
         super.serializeCustom(pObject, pContainer, pConditions);
         pObject.addProperty("name", pContainer.name.toString());
      }

      protected DynamicSpawn deserialize(JsonObject pObject, JsonDeserializationContext pContext, int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(pObject, "name"));
         return new DynamicSpawn(resourcelocation, pWeight, pQuality, pConditions, pFunctions);
      }
   }
}