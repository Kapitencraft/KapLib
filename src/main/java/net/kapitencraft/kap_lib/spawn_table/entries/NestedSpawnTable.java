package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A loot pool entry container that generates loot by referencing another loot table.
 */
public class NestedSpawnTable extends SpawnPoolSingletonContainer {
   public static final MapCodec<NestedSpawnTable> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
           Codec.either(ResourceKey.codec(), SpawnTable.DIRECT_CODEC).fieldOf("value").forGetter(f -> f.entry)
   ).and(singletonFields(i)).apply(i, NestedSpawnTable::new));


   final Either<ResourceKey<SpawnTable>, SpawnTable> entry;

   NestedSpawnTable(Either<ResourceKey<SpawnTable>, SpawnTable> pLootTableId, int pWeight, int pQuality, List<LootItemCondition> pConditions, List<SpawnEntityFunction> pFunctions) {
      super(pWeight, pQuality, pConditions, pFunctions);
      this.entry = pLootTableId;
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
      SpawnTable spawnTable = this.entry.map(k -> pLootContext.getSpawnTableManager().getSpawnTable(k.location()), Function.identity());
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
              new NestedSpawnTable(pTable, p_79780_, p_79781_, p_79782_, p_79783_));
   }
}