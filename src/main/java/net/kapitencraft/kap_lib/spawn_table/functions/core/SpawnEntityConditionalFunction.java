package net.kapitencraft.kap_lib.spawn_table.functions.core;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A LootItemFunction that only modifies the stacks if a list of {@linkplain LootItemCondition predicates} passes.
 */
public abstract class SpawnEntityConditionalFunction implements SpawnEntityFunction {
   protected final LootItemCondition[] predicates;
   private final Predicate<LootContext> compositePredicates;

   protected SpawnEntityConditionalFunction(LootItemCondition[] pPredicates) {
      this.predicates = pPredicates;
      this.compositePredicates = LootItemConditions.andConditions(pPredicates);
   }

   protected static void logWrongType(String name, Entity entity) {
      KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "{} was no {}", entity, name);
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

      for(int i = 0; i < this.predicates.length; ++i) {
         this.predicates[i].validate(pContext.forChild(".conditions[" + i + "]"));
      }

   }

   protected static SpawnEntityConditionalFunction.Builder<?> simpleBuilder(Function<LootItemCondition[], SpawnEntityFunction> pConstructor) {
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

      protected LootItemCondition[] getConditions() {
         return this.conditions.toArray(new LootItemCondition[0]);
      }
   }

   static final class DummyBuilder extends SpawnEntityConditionalFunction.Builder<SpawnEntityConditionalFunction.DummyBuilder> {
      private final Function<LootItemCondition[], SpawnEntityFunction> constructor;

      public DummyBuilder(Function<LootItemCondition[], SpawnEntityFunction> pConstructor) {
         this.constructor = pConstructor;
      }

      protected SpawnEntityConditionalFunction.DummyBuilder getThis() {
         return this;
      }

      public SpawnEntityFunction build() {
         return this.constructor.apply(this.getConditions());
      }
   }

   public abstract static class Serializer<T extends SpawnEntityConditionalFunction> implements net.minecraft.world.level.storage.loot.Serializer<T> {
      /**
       * Serialize the {@link CopyNbtFunction} by putting its data into the JsonObject.
       */
      public void serialize(JsonObject pJson, T pFunction, JsonSerializationContext pSerializationContext) {
         if (!ArrayUtils.isEmpty(pFunction.predicates)) {
            pJson.add("conditions", pSerializationContext.serialize(pFunction.predicates));
         }

      }

      /**
       * Deserialize a value by reading it from the JsonObject.
       */
      public final T deserialize(JsonObject pJson, JsonDeserializationContext pSerializationContext) {
         LootItemCondition[] alootitemcondition = GsonHelper.getAsObject(pJson, "conditions", new LootItemCondition[0], pSerializationContext, LootItemCondition[].class);
         return this.deserialize(pJson, pSerializationContext, alootitemcondition);
      }

      public abstract T deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions);
   }
}