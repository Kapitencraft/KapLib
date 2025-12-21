package net.kapitencraft.kap_lib.spawn_table.datagen;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.math.MethodsReturnNonnullByDefault;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.registry.SpawnTableRegistries;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpawnTableProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackOutput.PathProvider pathProvider;
   private final Set<ResourceKey<SpawnTable>> requiredTables;
   private final List<SpawnTableProvider.SubProviderEntry> subProviders;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public SpawnTableProvider(PackOutput pOutput, Set<ResourceKey<SpawnTable>> pRequiredTables, List<SpawnTableProvider.SubProviderEntry> pSubProviders, CompletableFuture<HolderLookup.Provider> registries) {
      this.pathProvider = pOutput.createPathProvider(PackOutput.Target.DATA_PACK, "kap_lib/spawn_tables");
      this.subProviders = pSubProviders;
      this.requiredTables = pRequiredTables;
      this.registries = registries;
   }

   public CompletableFuture<?> run(CachedOutput pOutput) {
      return this.registries.thenCompose(p -> this.run(pOutput, p));
   }

   public CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
      WritableRegistry<SpawnTable> writableregistry = new MappedRegistry<>(SpawnTableRegistries.Keys.SPAWN_TABLES, Lifecycle.experimental());
      Map<RandomSupport.Seed128bit, ResourceLocation> map = new Object2ObjectOpenHashMap<>();
      this.getTables().forEach((p_329847_) -> (p_329847_.provider().apply(provider)).generate((p_335199_, p_335200_) -> {
         ResourceLocation resourcelocation = p_335199_.location();
         ResourceLocation resourcelocation1 = map.put(RandomSequence.seedForKey(resourcelocation), resourcelocation);
         if (resourcelocation1 != null) {
            String var10000 = String.valueOf(resourcelocation1);
            Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + var10000 + " and " + String.valueOf(p_335199_.location()));
         }

         p_335200_.setRandomSequence(resourcelocation);
         SpawnTable loottable = p_335200_.setParamSet(p_329847_.paramSet).build();
         writableregistry.register(p_335199_, loottable, RegistrationInfo.BUILT_IN);
      }));
      writableregistry.freeze();
      ProblemReporter.Collector problemreporter$collector = new ProblemReporter.Collector();
      HolderGetter.Provider holdergetter$provider = (new RegistryAccess.ImmutableRegistryAccess(List.of(writableregistry))).freeze().asGetterLookup();
      ValidationContext validationcontext = new ValidationContext(problemreporter$collector, LootContextParamSets.ALL_PARAMS, holdergetter$provider);
      this.validate(writableregistry, validationcontext, problemreporter$collector);
      Multimap<String, String> multimap = problemreporter$collector.get();
      if (!multimap.isEmpty()) {
         multimap.forEach((p_124446_, p_124447_) -> LOGGER.warn("Found validation problem in {}: {}", p_124446_, p_124447_));
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         return CompletableFuture.allOf(writableregistry.entrySet().stream().map((p_335193_) -> {
            ResourceKey<SpawnTable> resourcekey1 = p_335193_.getKey();
            SpawnTable loottable = p_335193_.getValue();
            Path path = this.pathProvider.json(resourcekey1.location());
            return DataProvider.saveStable(output, provider, SpawnTable.DIRECT_CODEC, loottable, path);
         }).toArray(CompletableFuture[]::new));
      }

   }

   public List<SpawnTableProvider.SubProviderEntry> getTables() {
      return this.subProviders;
   }


   protected void validate(WritableRegistry<SpawnTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {

       for (ResourceKey<SpawnTable> spawnTableResourceKey : Sets.difference(this.requiredTables, writableregistry.registryKeySet())) {
           problemreporter$collector.report("Missing built-in table: " + spawnTableResourceKey.location());
       }

      writableregistry.holders().forEach((p_335195_) -> p_335195_.value().validate(validationcontext.setParams(p_335195_.value().getParamSet()).enterElement("{" + p_335195_.key().location() + "}", p_335195_.key())));
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public final @NotNull String getName() {
      return "Spawn Tables";
   }

   public record SubProviderEntry(Function<HolderLookup.Provider, SubProvider> provider, LootContextParamSet paramSet) {
   }

   @FunctionalInterface
   protected interface SubProvider {
      /**
       * @param pOutput a BiConsumer. accepts random sequence and SpawnTable builders
       */
      void generate(BiConsumer<ResourceKey<SpawnTable>, SpawnTable.Builder> pOutput);
   }
}
