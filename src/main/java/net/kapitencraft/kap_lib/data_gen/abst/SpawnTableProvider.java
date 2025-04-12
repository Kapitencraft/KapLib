package net.kapitencraft.kap_lib.data_gen.abst;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SpawnTableProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackOutput.PathProvider pathProvider;
   private final Set<ResourceLocation> requiredTables;
   private final List<SpawnTableProvider.SubProviderEntry> subProviders;

   public SpawnTableProvider(PackOutput pOutput, Set<ResourceLocation> pRequiredTables, List<SpawnTableProvider.SubProviderEntry> pSubProviders) {
      this.pathProvider = pOutput.createPathProvider(PackOutput.Target.DATA_PACK, "spawn_tables");
      this.subProviders = pSubProviders;
      this.requiredTables = pRequiredTables;
   }

   public CompletableFuture<?> run(CachedOutput pOutput) {
      final Map<ResourceLocation, SpawnTable> map = Maps.newHashMap();
      Map<RandomSupport.Seed128bit, ResourceLocation> map1 = new Object2ObjectOpenHashMap<>();
      this.getTables().forEach((subEntry) ->
              subEntry.provider().get().generate((sequence, builder) -> {
         ResourceLocation location = map1.put(RandomSequence.seedForKey(sequence), sequence);
         if (location != null) {
            Util.logAndPauseIfInIde("Spawn table random sequence seed collision on " + location + " and " + sequence);
         }

         builder.setRandomSequence(sequence);
         if (map.put(sequence, builder.setParamSet(subEntry.paramSet).build()) != null) {
            throw new IllegalStateException("Duplicate loot table " + sequence);
         }
      }));
      ValidationContext validationcontext = new ValidationContext(LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
         @Nullable
         public <T> T getElement(LootDataId<T> pId) {
            return (T)(pId.type() == LootDataType.TABLE ? map.get(pId.location()) : null);
         }
      });

      validate(map, validationcontext);

      Multimap<String, String> multimap = validationcontext.getProblems();
      if (!multimap.isEmpty()) {
         multimap.forEach((p_124446_, p_124447_) -> {
            LOGGER.warn("Found validation problem in {}: {}", p_124446_, p_124447_);
         });
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf(map.entrySet().stream().map((p_278900_) -> {
            ResourceLocation key = p_278900_.getKey();
            SpawnTable table = p_278900_.getValue();
            Path path = this.pathProvider.json(key);
            return DataProvider.saveStable(pOutput, SpawnTable.DATA_TYPE.parser().toJsonTree(table), path);
         }).toArray(CompletableFuture[]::new));
      }
   }

   public List<SpawnTableProvider.SubProviderEntry> getTables() {
      return this.subProviders;
   }

   protected void validate(Map<ResourceLocation, SpawnTable> map, ValidationContext validationcontext) {
      for(ResourceLocation resourcelocation : Sets.difference(this.requiredTables, map.keySet())) {
         validationcontext.reportProblem("Missing built-in table: " + resourcelocation);
      }

      map.forEach((p_278897_, p_278898_) -> {
         p_278898_.validate(validationcontext.setParams(p_278898_.getParamSet()).enterElement("{" + p_278897_ + "}", new LootDataId<>(LootDataType.TABLE, p_278897_)));
      });
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public final String getName() {
      return "Spawn Tables";
   }

   public record SubProviderEntry(Supplier<SubProvider> provider, LootContextParamSet paramSet) {
   }

   @FunctionalInterface
   protected interface SubProvider {
      /**
       * @param pOutput a BiConsumer. accepts random sequence and SpawnTable builders
       */
      void generate(BiConsumer<ResourceLocation, SpawnTable.Builder> pOutput);
   }
}
