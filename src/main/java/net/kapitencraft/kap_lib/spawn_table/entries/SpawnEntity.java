package net.kapitencraft.kap_lib.spawn_table.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

/**
 * A loot pool entry that always generates a given item.
 */
public class SpawnEntity extends SpawnPoolSingletonContainer {
   public static final MapCodec<SpawnEntity> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
           BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(f -> f.entityType)
   ).and(singletonFields(i)).apply(i, SpawnEntity::new));

   final EntityType<?> entityType;

   SpawnEntity(EntityType<?> pEntity, int pWeight, int pQuality, List<LootItemCondition> pConditions, List<SpawnEntityFunction> pFunctions) {
      super(pWeight, pQuality, pConditions, pFunctions);
      this.entityType = pEntity;
   }

   public SpawnPoolEntryType getType() {
      return SpawnPoolEntries.ENTITY.get();
   }

   /**
    * Generate the loot entities of this entry.
    * Contrary to the method name this method does not always generate one stack, it can also generate zero or multiple
    * stacks.
    */
   public void createEntity(Consumer<Entity> pEntityConsumer, SpawnContext pLootContext) {
      Entity entity = this.entityType.create(pLootContext.getLevel());
      if (entity == null) KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "entity could not be spawned!");
      pEntityConsumer.accept(entity);
   }

   public static Builder<?> spawnTableEntity(EntityType<?> pEntity) {
      return simpleBuilder((p_79583_, p_79584_, p_79585_, p_79586_) ->
              new SpawnEntity(pEntity, p_79583_, p_79584_, p_79585_, p_79586_)
      );
   }
}