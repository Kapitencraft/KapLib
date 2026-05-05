package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.minecraft.Util;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Predicate;

/**
 * Base class for loot pool entry containers. This class just stores a list of conditions that are checked before the
 * entry generates loot.
 */
public abstract class SpawnPoolEntryContainer implements ComposableEntryContainer {
   /** Conditions for the loot entry to be applied. */
   protected final List<LootItemCondition> conditions;
   private final Predicate<LootContext> compositeCondition;

   protected SpawnPoolEntryContainer(List<LootItemCondition> pConditions) {
      this.conditions = pConditions;
      this.compositeCondition = Util.allOf(pConditions);
   }

   public void validate(ValidationContext pValidationContext) {
      for(int i = 0; i < this.conditions.size(); ++i) {
         this.conditions.get(i).validate(pValidationContext.forChild(".condition[" + i + "]"));
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

      protected List<LootItemCondition> getConditions() {
         return this.conditions;
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


   protected static <T extends SpawnPoolEntryContainer> Products.P1<RecordCodecBuilder.Mu<T>, List<LootItemCondition>> commonFields(RecordCodecBuilder.Instance<T> instance) {
      return instance.group(LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(p_298548_ -> p_298548_.conditions));
   }
}