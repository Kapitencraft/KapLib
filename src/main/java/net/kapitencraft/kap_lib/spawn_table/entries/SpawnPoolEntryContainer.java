package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * Base class for loot pool entry containers. This class just stores a list of conditions that are checked before the
 * entry generates loot.
 */
public abstract class SpawnPoolEntryContainer implements ComposableEntryContainer {
   /** Conditions for the loot entry to be applied. */
   protected final LootItemCondition[] conditions;
   private final Predicate<LootContext> compositeCondition;

   protected SpawnPoolEntryContainer(LootItemCondition[] pConditions) {
      this.conditions = pConditions;
      this.compositeCondition = LootItemConditions.andConditions(pConditions);
   }

   public void validate(ValidationContext pValidationContext) {
      for(int i = 0; i < this.conditions.length; ++i) {
         this.conditions[i].validate(pValidationContext.forChild(".condition[" + i + "]"));
      }

   }

   protected final boolean canRun(SpawnContext pLootContext) {
      return this.compositeCondition.test(pLootContext);
   }

   public abstract SpawnPoolEntryType getType();

   public abstract static class Builder<T extends Builder<T>> implements ConditionUserBuilder<T> {
      private final List<LootItemCondition> conditions = Lists.newArrayList();

      protected abstract T getThis();

      public T when(LootItemCondition.Builder pConditionBuilder) {
         this.conditions.add(pConditionBuilder.build());
         return this.getThis();
      }

      public final T unwrap() {
         return this.getThis();
      }

      protected LootItemCondition[] getConditions() {
         return this.conditions.toArray(new LootItemCondition[0]);
      }

      public AlternativesEntry.Builder otherwise(Builder<?> pChildBuilder) {
         return new AlternativesEntry.Builder(this, pChildBuilder);
      }

      public EntryGroup.Builder append(Builder<?> pChildBuilder) {
         return new EntryGroup.Builder(this, pChildBuilder);
      }

      public SequentialEntry.Builder then(Builder<?> pChildBuilder) {
         return new SequentialEntry.Builder(this, pChildBuilder);
      }

      public abstract SpawnPoolEntryContainer build();
   }

   public abstract static class Serializer<T extends SpawnPoolEntryContainer> implements net.minecraft.world.level.storage.loot.Serializer<T> {
      /**
       * Serialize the {@link CopyNbtFunction} by putting its data into the JsonObject.
       */
      public final void serialize(JsonObject pJson, T pValue, JsonSerializationContext pSerializationContext) {
         if (!ArrayUtils.isEmpty(pValue.conditions)) {
            pJson.add("conditions", pSerializationContext.serialize(pValue.conditions));
         }

         this.serializeCustom(pJson, pValue, pSerializationContext);
      }

      /**
       * Deserialize a value by reading it from the JsonObject.
       */
      public final T deserialize(JsonObject pJson, JsonDeserializationContext pSerializationContext) {
         LootItemCondition[] alootitemcondition = GsonHelper.getAsObject(pJson, "conditions", new LootItemCondition[0], pSerializationContext, LootItemCondition[].class);
         return this.deserializeCustom(pJson, pSerializationContext, alootitemcondition);
      }

      public abstract void serializeCustom(JsonObject pObject, T pContainer, JsonSerializationContext pConditions);

      public abstract T deserializeCustom(JsonObject pObject, JsonDeserializationContext pContext, LootItemCondition[] pConditions);
   }
}