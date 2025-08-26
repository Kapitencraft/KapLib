package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Consumer;

/**
 * A loot pool entry container that generates loot by referencing another loot table.
 */
public class SpawnTableReference extends SpawnPoolSingletonContainer {

   final ResourceLocation name;

   SpawnTableReference(ResourceLocation pLootTableId, int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions) {
      super(pWeight, pQuality, pConditions, pFunctions);
      this.name = pLootTableId;
   }

   public SpawnPoolEntryType getType() {
      return SpawnPoolEntries.REFERENCE.get();
   }

   /**
    * Generate the loot stacks of this entry.
    * Contrary to the method name this method does not always generate one stack, it can also generate zero or multiple
    * stacks.
    */
   public void createEntity(Consumer<Entity> pStackConsumer, SpawnContext pLootContext) {
      SpawnTable spawnTable = pLootContext.getSpawnTableManager().getSpawnTable(this.name);
      if (spawnTable == null) KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "unknown spawn table: {}", this.name);
      else spawnTable.getRandomEntities(pLootContext, pStackConsumer);
   }

   public void validate(ValidationContext pValidationContext) {
      LootDataId<LootTable> lootdataid = new LootDataId<>(LootDataType.TABLE, this.name);
      if (pValidationContext.hasVisitedElement(lootdataid)) {
         pValidationContext.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(pValidationContext);
         pValidationContext.resolver().getElementOptional(lootdataid).ifPresentOrElse((p_279078_) -> {
            p_279078_.validate(pValidationContext.enterElement("->{" + this.name + "}", lootdataid));
         }, () -> {
            pValidationContext.reportProblem("Unknown loot table called " + this.name);
         });
      }
   }

   public static Builder<?> spawnTableReference(ResourceLocation pTable) {
      return simpleBuilder((p_79780_, p_79781_, p_79782_, p_79783_) ->
              new SpawnTableReference(pTable, p_79780_, p_79781_, p_79782_, p_79783_));
   }

   public static class Serializer extends SpawnPoolSingletonContainer.Serializer<SpawnTableReference> {
      public void serializeCustom(JsonObject pObject, SpawnTableReference pContainer, JsonSerializationContext pConditions) {
         super.serializeCustom(pObject, pContainer, pConditions);
         pObject.addProperty("name", pContainer.name.toString());
      }

      protected SpawnTableReference deserialize(JsonObject pObject, JsonDeserializationContext pContext, int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(pObject, "name"));
         return new SpawnTableReference(resourcelocation, pWeight, pQuality, pConditions, pFunctions);
      }
   }
}