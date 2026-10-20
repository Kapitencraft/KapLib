package net.kapitencraft.kap_lib.spawn_table.entries;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.spawn_table.registry.SpawnTableRegistries;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A loot pool entry container that generates loot by referencing another loot table.
 */
public class NestedSpawnTable extends SpawnPoolSingletonContainer {
   public static final MapCodec<NestedSpawnTable> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
           SpawnTable.GATHER_CODEC.fieldOf("value").forGetter(f -> f.entry)
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
   public void createEntity(Consumer<Entity> stackConsumer, SpawnContext lootContext) {
      this.entry.map(
              (p_335324_) -> lootContext.getResolver().get(SpawnTableRegistries.Keys.SPAWN_TABLES, p_335324_).map(Holder::value).orElse(SpawnTable.EMPTY),
              Function.identity()).getRandomEntitiesRaw(lootContext, stackConsumer);
   }

   public void validate(ValidationContext validationContext) {
      Optional<ResourceKey<SpawnTable>> optional = this.entry.left();
      if (optional.isPresent()) {
         ResourceKey<SpawnTable> resourcekey = optional.get();
         if (!validationContext.allowsReferences()) {
            validationContext.reportProblem("Uses reference to " + resourcekey.location() + ", but references are not allowed");
            return;
         }

         if (validationContext.hasVisitedElement(resourcekey)) {
            validationContext.reportProblem("Table " + resourcekey.location() + " is recursively called");
            return;
         }
      }

      super.validate(validationContext);
      this.entry.ifLeft((p_335332_) -> validationContext.resolver().get(SpawnTableRegistries.Keys.SPAWN_TABLES, p_335332_).ifPresentOrElse((p_339565_) -> p_339565_.value().validate(validationContext.enterElement("->{" + p_335332_.location() + "}", p_335332_)), () -> validationContext.reportProblem("Unknown loot table called " + p_335332_.location()))).ifRight((p_331183_) -> p_331183_.validate(validationContext.forChild("->{inline}")));
   }

   public static Builder<?> spawnTableReference(ResourceKey<SpawnTable> pTable) {
      return simpleBuilder((p_79780_, p_79781_, p_79782_, p_79783_) ->
              new NestedSpawnTable(Either.left(pTable), p_79780_, p_79781_, p_79782_, p_79783_)
      );
   }

   public static Builder<?> inlineSpawnTable(SpawnTable table) {
      return simpleBuilder((pWeight, pQuality, pConditions, pFunctions) ->
              new NestedSpawnTable(Either.right(table), pWeight, pQuality, pConditions, pFunctions)
      );
   }
}