package net.kapitencraft.kap_lib.spawn_table;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.spawn_table.registry.SpawnTableRegistries;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.functions.core.FunctionUserBuilder;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SpawnTable {
   public static final LootContextParamSet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
   public static final Codec<SpawnTable> DIRECT_CODEC = RecordCodecBuilder.create(
           p_338123_ -> p_338123_.group(
                           LootContextParamSets.CODEC.lenientOptionalFieldOf("type", DEFAULT_PARAM_SET).forGetter(p_298001_ -> p_298001_.paramSet),
                           ResourceLocation.CODEC.optionalFieldOf("random_sequence").forGetter(p_297998_ -> Optional.ofNullable(p_297998_.randomSequence)),
                           MiscHelper.spawnPoolsCodec(SpawnPool::setName).optionalFieldOf("pools", List.of()).forGetter(p_298002_ -> p_298002_.pools),
                           net.neoforged.neoforge.common.conditions.ConditionalOps.decodeListWithElementConditions(SpawnEntityFunctions.ROOT_CODEC).optionalFieldOf("functions", List.of()).forGetter(p_298000_ -> p_298000_.functions)
                   )
                   .apply(p_338123_, SpawnTable::new)
   );
   public static final Codec<Holder<SpawnTable>> CODEC = RegistryFileCodec.create(SpawnTableRegistries.Keys.SPAWN_TABLES, DIRECT_CODEC);
   public static final Codec<Either<ResourceKey<SpawnTable>, SpawnTable>> GATHER_CODEC = Codec.either(ResourceKey.codec(SpawnTableRegistries.Keys.SPAWN_TABLES), SpawnTable.DIRECT_CODEC);

   //public static final LootDataType<SpawnTable> DATA_TYPE = new LootDataType<>(PARSER, SpawnTableProvider::getSpawnTableSerializer, "spawn_tables", createValidator());

   static final Logger LOGGER = LogUtils.getLogger();
   public static final SpawnTable EMPTY = new SpawnTable(LootContextParamSets.EMPTY, Optional.empty(), List.of(), List.of());
   final LootContextParamSet paramSet;
   final ResourceLocation randomSequence;
   private final List<SpawnPool> pools;
   final List<SpawnEntityFunction> functions;
   private final BiFunction<Entity, SpawnContext, Entity> compositeFunction;

   SpawnTable(LootContextParamSet pParamSet, Optional<ResourceLocation> pRandomSequence, List<SpawnPool> pPools, List<SpawnEntityFunction> pFunctions) {
      this.paramSet = pParamSet;
      this.randomSequence = pRandomSequence.orElse(null);
      this.pools = Lists.newArrayList(pPools);
      this.functions = pFunctions;
      this.compositeFunction = SpawnEntityFunctions.compose(pFunctions);
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
      //LootContext.VisitedEntry<?> visitedEntry = this.createContextVisitedEntry();
      //if (pContext.pushVisitedElement(visitedEntry)) {
         Consumer<Entity> consumer = SpawnEntityFunction.decorate(this.compositeFunction, objectarraylist::add, pContext);

         for(SpawnPool pool : this.pools) {
            pool.addRandomEntities(consumer, pContext);
         }

      //   pContext.popVisitedElement(visitedEntry);
      //} else {
      //   LOGGER.warn("Detected infinite loop in loot tables");
      //}
      return objectarraylist;
   }


   public void getRandomEntitiesRaw(SpawnContext context, Consumer<Entity> output) {
      Consumer<Entity> consumer = SpawnEntityFunction.decorate(this.compositeFunction, output, context);

      for(SpawnPool pool : this.pools) {
         pool.addRandomEntities(consumer, context);
      }
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

      for(int j = 0; j < this.functions.size(); ++j) {
         this.functions.get(j).validate(pValidator.forChild(".functions[" + j + "]"));
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
         return new SpawnTable(this.paramSet, Optional.ofNullable(this.randomSequence), this.pools, this.functions);
      }
   }
}
