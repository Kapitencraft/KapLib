package net.kapitencraft.kap_lib.spawn_table.functions.core;

import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Base interface for builders that accept loot functions.
 * 
 * @see LootItemFunction
 */
public interface FunctionUserBuilder<T extends FunctionUserBuilder<T>> {
   T apply(SpawnEntityFunction.Builder pFunctionBuilder);

   default <E> T apply(Iterable<E> pBuilderSources, Function<E, SpawnEntityFunction.Builder> pToBuilderFunction) {
      T t = this.unwrap();

      for(E e : pBuilderSources) {
         t = t.apply(pToBuilderFunction.apply(e));
      }

      return t;
   }

   default <E> T apply(E[] pBuilderSources, Function<E, SpawnEntityFunction.Builder> pToBuilderFunction) {
      return this.apply(Arrays.asList(pBuilderSources), pToBuilderFunction);
   }

   T unwrap();
}