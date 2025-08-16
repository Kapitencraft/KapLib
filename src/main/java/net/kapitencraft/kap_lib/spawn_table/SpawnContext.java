package net.kapitencraft.kap_lib.spawn_table;

import net.kapitencraft.kap_lib.spawn_table.entries.DynamicSpawn;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * LootContext stores various context information for loot generation.
 * This includes the Level as well as any known {@link LootContextParam}s.
 */
public class SpawnContext extends LootContext {
   private final Map<ResourceLocation, DynamicSpawn> dynamicSpawns = new HashMap<>();
   private final SpawnTableManager manager;


   SpawnContext(LootParams params, RandomSource randomsource, HolderGetter.Provider provider, SpawnTableManager manager) {
      super(params, randomsource, provider);
      this.manager = manager;
   }

   public void addDynamicSpawn(ResourceLocation pName, Consumer<Entity> pConsumer) {
      DynamicSpawn spawn = this.dynamicSpawns.get(pName);
      if (spawn != null) spawn.createEntity(pConsumer, this);
   }

   public SpawnTableManager getSpawnTableManager() {
      return manager;
   }

   public static class Builder {
      private final LootParams params;
      @Nullable
      private RandomSource random;
      private ResourceLocation queriedLootTableId; // Forge: correctly pass around loot table ID with copy constructor

      public Builder(LootParams pParams) {
         this.params = pParams;
      }

      public Builder(SpawnContext context) {
         this.params = context.params;
         this.random = context.random;
      }

      public SpawnContext.Builder withOptionalRandomSeed(long pSeed) {
         if (pSeed != 0L) {
            this.random = RandomSource.create(pSeed);
         }

         return this;
      }

      public SpawnContext.Builder withQueriedLootTableId(ResourceLocation queriedLootTableId) {
         this.queriedLootTableId = queriedLootTableId;
         return this;
      }

      public ServerLevel getLevel() {
         return this.params.getLevel();
      }

      public SpawnContext create(@Nullable ResourceLocation pRandomLocation) {
         ServerLevel serverlevel = this.getLevel();
         MinecraftServer minecraftserver = serverlevel.getServer();
         RandomSource randomsource;
         if (this.random != null) {
            randomsource = this.random;
         } else if (pRandomLocation != null) {
            randomsource = serverlevel.getRandomSequence(pRandomLocation);
         } else {
            randomsource = serverlevel.getRandom();
         }

         return new SpawnContext(this.params, randomsource, minecraftserver.getLootData(), queriedLootTableId, SpawnTableManager.instance);
      }
   }
}
