package net.kapitencraft.kap_lib.spawn_table;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
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
    private final Map<ResourceLocation, DynamicEntity> dynamicSpawns;

    SpawnContext(LootParams params, RandomSource randomsource, HolderGetter.Provider provider, Map<ResourceLocation, DynamicEntity> map) {
        super(params, randomsource, provider);
        this.dynamicSpawns = ImmutableMap.copyOf(map);
    }

    public void addDynamicSpawn(ResourceLocation pName, Consumer<Entity> pConsumer) {
        DynamicEntity spawn = this.dynamicSpawns.get(pName);
        if (spawn != null) spawn.add(pConsumer);
    }

    public static class Builder {
        private final LootParams params;
        @Nullable
        private RandomSource random;
        private Map<ResourceLocation, DynamicEntity> dynamicSpawns = new HashMap<>();

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

        public SpawnContext.Builder withDynamicEntity(ResourceLocation name, SpawnContext.DynamicEntity dynamic) {
            this.dynamicSpawns.put(name, dynamic);
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

            return new SpawnContext(this.params, randomsource, minecraftserver.reloadableRegistries().lookup(), this.dynamicSpawns);
        }
    }

    public interface DynamicEntity {
        void add(Consumer<Entity> output);
    }
}
