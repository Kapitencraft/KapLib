package net.kapitencraft.kap_lib.spawn_table.functions.core;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;

/**
 * The SerializerType for {@link SpawnEntityFunction}.
 */
public class SpawnEntityFunctionType extends SerializerType<SpawnEntityFunction> {
   public SpawnEntityFunctionType(Serializer<? extends SpawnEntityFunction> pSerializer) {
      super(pSerializer);
   }
}