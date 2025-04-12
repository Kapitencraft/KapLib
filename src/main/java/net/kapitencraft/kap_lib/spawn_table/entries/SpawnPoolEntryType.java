package net.kapitencraft.kap_lib.spawn_table.entries;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

/**
 * The SerializerType for {@link SpawnPoolEntryContainer}.
 */
public class SpawnPoolEntryType extends SerializerType<SpawnPoolEntryContainer> {
   public SpawnPoolEntryType(Serializer<? extends SpawnPoolEntryContainer> pSerializer) {
      super(pSerializer);
   }
}