package net.kapitencraft.kap_lib.spawn_table;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.functions.core.FunctionUserBuilder;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SpawnTable {
   public static final LootDataType<SpawnTable> DATA_TYPE = new LootDataType<>(SpawnDeserializers.createSpawnTableSerializer().create(), SpawnTableProvider::getSpawnTableSerializer, "spawn_tables", createValidator());

   private static LootDataType.Validator<SpawnTable> createValidator() {
      return (context, dataId, table) ->
              table.validate(context.setParams(table.getParamSet()).enterElement("{" + dataId.type().directory() + ":" + dataId.location() + "}", dataId));
   }

   static final Logger LOGGER = LogUtils.getLogger();
   public static final SpawnTable EMPTY = new SpawnTable(LootContextParamSets.EMPTY, null, new SpawnPool[0], new SpawnEntityFunction[0]);
   public static final LootContextParamSet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
   final LootContextParamSet paramSet;
   @Nullable
   final ResourceLocation randomSequence;
   private final List<SpawnPool> pools;
   final SpawnEntityFunction[] functions;
   private final BiFunction<Entity, SpawnContext, Entity> compositeFunction;

   SpawnTable(LootContextParamSet pParamSet, @Nullable ResourceLocation pRandomSequence, SpawnPool[] pPools, SpawnEntityFunction[] pFunctions) {
      this.paramSet = pParamSet;
      this.randomSequence = pRandomSequence;
      this.pools = Lists.newArrayList(pPools);
      this.functions = pFunctions;
      this.compositeFunction = SpawnEntityFunctions.compose(pFunctions);
   }

   private LootContext.VisitedEntry<?> createContextVisitedEntry() {
      return new LootContext.VisitedEntry<>(DATA_TYPE, this);
   }

   public void getRandomEntities(LootParams pParams, long pSeed, Consumer<Entity> pOutput) {
      this.getRandomEntities((new SpawnContext.Builder(pParams)).withOptionalRandomSeed(pSeed).create(this.randomSequence)).forEach(pOutput);
   }

   public void getRandomEntities(LootParams pParams, Consumer<Entity> pOutput) {
      this.getRandomEntities(pParams).forEach(pOutput);
   }

   /**
    * Generate random items to the given Consumer, ensuring they do not exceed their maximum stack size.
    */
   public void getRandomEntities(SpawnContext pContextData, Consumer<Entity> pOutput) {
      this.getRandomEntities(pContextData).forEach((entity) -> {
         pContextData.getLevel().addFreshEntity(entity);
         pOutput.accept(entity);
      });
   }

   public ObjectArrayList<Entity> getRandomEntities(LootParams pParams, long pSeed) {
      return this.getRandomEntities((new SpawnContext.Builder(pParams)).withOptionalRandomSeed(pSeed).create(this.randomSequence));
   }

   public ObjectArrayList<Entity> getRandomEntities(LootParams pParams) {
      return this.getRandomEntities((new SpawnContext.Builder(pParams)).create(this.randomSequence));
   }

   /**
    * Generate random items to a List.
    */
   private ObjectArrayList<Entity> getRandomEntities(SpawnContext pContext) {
      ObjectArrayList<Entity> objectarraylist = new ObjectArrayList<>();
      LootContext.VisitedEntry<?> visitedentry = this.createContextVisitedEntry();
      if (pContext.pushVisitedElement(visitedentry)) {
         Consumer<Entity> consumer = SpawnEntityFunction.decorate(this.compositeFunction, objectarraylist::add, pContext);

         for(SpawnPool pool : this.pools) {
            pool.addRandomEntities(consumer, pContext);
         }

         pContext.popVisitedElement(visitedentry);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }
      return objectarraylist;
   }

   /**
    * Get the parameter set for this LootTable.
    */
   public LootContextParamSet getParamSet() {
      return this.paramSet;
   }

   /**
    * Validate this LootTable using the given ValidationContext.
    */
   public void validate(ValidationContext pValidator) {
      for(int i = 0; i < this.pools.size(); ++i) {
         this.pools.get(i).validate(pValidator.forChild(".pools[" + i + "]"));
      }

      for(int j = 0; j < this.functions.length; ++j) {
         this.functions[j].validate(pValidator.forChild(".functions[" + j + "]"));
      }

   }

   public static SpawnTable.Builder spawnTable() {
      return new SpawnTable.Builder();
   }

   //======================== FORGE START =============================================
   private boolean isFrozen = false;
   public void freeze() {
      this.isFrozen = true;
      this.pools.forEach(SpawnPool::freeze);
   }
   public boolean isFrozen(){ return this.isFrozen; }
   private void checkFrozen() {
      if (this.isFrozen())
         throw new RuntimeException("Attempted to modify LootTable after being finalized!");
   }

   private ResourceLocation lootTableId;
   public void setId(final ResourceLocation id) {
      if (this.lootTableId != null) throw new IllegalStateException("Attempted to rename loot table from '" + this.lootTableId + "' to '" + id + "': this is not supported");
      this.lootTableId = java.util.Objects.requireNonNull(id);
   }
   public ResourceLocation getLootTableId() { return this.lootTableId; }

   @org.jetbrains.annotations.Nullable
   public SpawnPool getPool(String name) {
      return pools.stream().filter(e -> name.equals(e.getName())).findFirst().orElse(null);
   }

   @org.jetbrains.annotations.Nullable
   public SpawnPool removePool(String name) {
      checkFrozen();
      for (SpawnPool pool : this.pools) {
         if (name.equals(pool.getName())) {
            this.pools.remove(pool);
            return pool;
         }
      }
      return null;
   }

   public void addPool(SpawnPool pool) {
      checkFrozen();
      if (pools.stream().anyMatch(e -> e == pool || e.getName() != null && e.getName().equals(pool.getName())))
         throw new RuntimeException("Attempted to add a duplicate pool to loot table: " + pool.getName());
      this.pools.add(pool);
   }
   //======================== FORGE END ===============================================

   public static class Builder implements FunctionUserBuilder<Builder> {
      private final List<SpawnPool> pools = Lists.newArrayList();
      private final List<SpawnEntityFunction> functions = Lists.newArrayList();
      private LootContextParamSet paramSet = SpawnTable.DEFAULT_PARAM_SET;
      @Nullable
      private ResourceLocation randomSequence = null;

      public SpawnTable.Builder withPool(SpawnPool.Builder pLootPool) {
         this.pools.add(pLootPool.build());
         return this;
      }

      public SpawnTable.Builder setParamSet(LootContextParamSet pParameterSet) {
         this.paramSet = pParameterSet;
         return this;
      }

      public SpawnTable.Builder setRandomSequence(ResourceLocation pRandomSequence) {
         this.randomSequence = pRandomSequence;
         return this;
      }

      public SpawnTable.Builder apply(SpawnEntityFunction.Builder pFunctionBuilder) {
         this.functions.add(pFunctionBuilder.build());
         return this;
      }

      public SpawnTable.Builder unwrap() {
         return this;
      }

      public SpawnTable build() {
         return new SpawnTable(this.paramSet, this.randomSequence, this.pools.toArray(new SpawnPool[0]), this.functions.toArray(new SpawnEntityFunction[0]));
      }
   }

   public static class Serializer implements JsonDeserializer<SpawnTable>, JsonSerializer<SpawnTable> {
      public SpawnTable deserialize(JsonElement pJson, Type pTypeOfT, JsonDeserializationContext pContext) throws JsonParseException {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(pJson, "loot table");
         SpawnPool[] alootpool = GsonHelper.getAsObject(jsonobject, "pools", new SpawnPool[0], pContext, SpawnPool[].class);
         LootContextParamSet lootcontextparamset = null;
         if (jsonobject.has("type")) {
            String s = GsonHelper.getAsString(jsonobject, "type");
            lootcontextparamset = LootContextParamSets.get(new ResourceLocation(s));
         }

         ResourceLocation resourcelocation;
         if (jsonobject.has("random_sequence")) {
            String s1 = GsonHelper.getAsString(jsonobject, "random_sequence");
            resourcelocation = new ResourceLocation(s1);
         } else {
            resourcelocation = null;
         }

         SpawnEntityFunction[] alootitemfunction = GsonHelper.getAsObject(jsonobject, "functions", new SpawnEntityFunction[0], pContext, SpawnEntityFunction[].class);
         return new SpawnTable(lootcontextparamset != null ? lootcontextparamset : LootContextParamSets.ALL_PARAMS, resourcelocation, alootpool, alootitemfunction);
      }

      public JsonElement serialize(SpawnTable pSrc, Type pTypeOfSrc, JsonSerializationContext pContext) {
         JsonObject jsonobject = new JsonObject();
         if (pSrc.paramSet != SpawnTable.DEFAULT_PARAM_SET) {
            ResourceLocation resourcelocation = LootContextParamSets.getKey(pSrc.paramSet);
            if (resourcelocation != null) {
               jsonobject.addProperty("type", resourcelocation.toString());
            } else {
               SpawnTable.LOGGER.warn("Failed to find id for param set {}", pSrc.paramSet);
            }
         }

         if (pSrc.randomSequence != null) {
            jsonobject.addProperty("random_sequence", pSrc.randomSequence.toString());
         }

         if (!pSrc.pools.isEmpty()) {
            jsonobject.add("pools", pContext.serialize(pSrc.pools));
         }

         if (!ArrayUtils.isEmpty(pSrc.functions)) {
            jsonobject.add("functions", pContext.serialize(pSrc.functions));
         }

         return jsonobject;
      }
   }
}
