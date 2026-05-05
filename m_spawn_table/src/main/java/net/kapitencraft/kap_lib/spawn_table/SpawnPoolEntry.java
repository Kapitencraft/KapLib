package net.kapitencraft.kap_lib.spawn_table;

import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

/**
 * A spawn pool entry generates zero or more entities based on the LootContext.
 * Each spawn pool entry has a weight that determines how likely it is to be generated within a given loot pool.
 */
public interface SpawnPoolEntry {
   /**
    * Gets the effective weight based on the loot entry's weight and quality multiplied by looter's luck.
    */
   int getWeight(float pLuck);

   /**
    * Generate the loot entities of this entry.
    * Contrary to the method name this method does not always generate one entity, it can also generate zero or multiple
    * entities.
    */
   void createEntity(Consumer<Entity> pStackConsumer, SpawnContext pLootContext);
}