package net.kapitencraft.kap_lib.spawn_table.functions.core;

import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.minecraft.world.entity.Entity;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * A LootItemFunction modifies an Entity based on the current SpawnContext.
 * 
 * @see SpawnEntityFunctions SpawnEntityFunctions
 */
public interface SpawnEntityFunction extends LootContextUser, BiFunction<Entity, SpawnContext, Entity> {
   SpawnEntityFunctionType getType();

   /**
    * Create a decorated Consumer. The resulting consumer will first apply {@code stackModification} to all stacks
    * before passing them on to {@code originalConsumer}.
    */
   static Consumer<Entity> decorate(BiFunction<Entity, SpawnContext, Entity> pStackModification, Consumer<Entity> pOriginalConsumer, SpawnContext pLootContext) {
      return (p_80732_) -> pOriginalConsumer.accept(pStackModification.apply(p_80732_, pLootContext));
   }

   interface Builder {
      SpawnEntityFunction build();
   }
}