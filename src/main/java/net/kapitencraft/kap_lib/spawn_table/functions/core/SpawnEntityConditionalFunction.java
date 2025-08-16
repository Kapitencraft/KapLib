package net.kapitencraft.kap_lib.spawn_table.functions.core;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A LootItemFunction that only modifies the stacks if a list of {@linkplain LootItemCondition predicates} passes.
 */
public abstract class SpawnEntityConditionalFunction implements SpawnEntityFunction {
   protected final List<LootItemCondition> predicates;
   private final Predicate<LootContext> compositePredicates;

   protected SpawnEntityConditionalFunction(List<LootItemCondition> pPredicates) {
      this.predicates = pPredicates;
      this.compositePredicates = Util.allOf(pPredicates);
   }

   protected static void logWrongType(String name, Entity entity) {
      KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "{} was no {}", entity, name);
   }

   protected static <T extends SpawnEntityConditionalFunction> Products.P1<RecordCodecBuilder.Mu<T>, List<LootItemCondition>> commonFields(RecordCodecBuilder.Instance<T> instance) {
      return instance.group(LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(p_299114_ -> p_299114_.predicates));
   }

   public final Entity apply(Entity pEntity, SpawnContext pContext) {
      return this.compositePredicates.test(pContext) ? this.run(pEntity, pContext) : pEntity;
   }

   /**
    * Called to perform the actual action of this function, after conditions have been checked.
    */
   protected abstract Entity run(Entity pEntity, SpawnContext pContext);

   /**
    * Validate that this object is used correctly according to the given ValidationContext.
    */
   public void validate(ValidationContext pContext) {
      SpawnEntityFunction.super.validate(pContext);

      for(int i = 0; i < this.predicates.size(); ++i) {
         this.predicates.get(i).validate(pContext.forChild(".conditions[" + i + "]"));
      }

   }

   protected static SpawnEntityConditionalFunction.Builder<?> simpleBuilder(Function<List<LootItemCondition>, SpawnEntityFunction> pConstructor) {
      return new SpawnEntityConditionalFunction.DummyBuilder(pConstructor);
   }

   public abstract static class Builder<T extends SpawnEntityConditionalFunction.Builder<T>> implements SpawnEntityFunction.Builder, ConditionUserBuilder<T> {
      private final List<LootItemCondition> conditions = Lists.newArrayList();

      public T when(LootItemCondition.Builder p_80694_) {
         this.conditions.add(p_80694_.build());
         return this.getThis();
      }

      public final T unwrap() {
         return this.getThis();
      }

      protected abstract T getThis();

      protected List<LootItemCondition> getConditions() {
         return this.conditions;
      }
   }

   static final class DummyBuilder extends SpawnEntityConditionalFunction.Builder<SpawnEntityConditionalFunction.DummyBuilder> {
      private final Function<List<LootItemCondition>, SpawnEntityFunction> constructor;

      public DummyBuilder(Function<List<LootItemCondition>, SpawnEntityFunction> pConstructor) {
         this.constructor = pConstructor;
      }

      protected SpawnEntityConditionalFunction.DummyBuilder getThis() {
         return this;
      }

      public SpawnEntityFunction build() {
         return this.constructor.apply(this.getConditions());
      }
   }
}