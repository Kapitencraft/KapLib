package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.common.collect.Lists;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * A composite spawn pool entry container that expands all its children in order until one of them succeeds.
 * This container succeeds if one of its children succeeds.
 */
public class AlternativesEntry extends CompositeEntryBase {
   public AlternativesEntry(SpawnPoolEntryContainer[] pChildren, LootItemCondition[] pConditions) {
      super(pChildren, pConditions);
   }

   public SpawnPoolEntryType getType() {
      return SpawnPoolEntries.ALTERNATIVES.get();
   }

   /**
    * Compose the given children into one container.
    */
   protected ComposableEntryContainer compose(ComposableEntryContainer[] pEntries) {
       return switch (pEntries.length) {
           case 0 -> ComposableEntryContainer.ALWAYS_FALSE;
           case 1 -> pEntries[0];
           case 2 -> pEntries[0].or(pEntries[1]);
           default -> (p_79393_, p_79394_) -> {
               for (ComposableEntryContainer composableentrycontainer : pEntries) {
                   if (composableentrycontainer.expand(p_79393_, p_79394_)) {
                       return true;
                   }
               }

               return false;
           };
       };
   }

   public void validate(ValidationContext pValidationContext) {
      super.validate(pValidationContext);

      for(int i = 0; i < this.children.length - 1; ++i) {
         if (ArrayUtils.isEmpty(this.children[i].conditions)) {
            pValidationContext.reportProblem("Unreachable entry!");
         }
      }

   }

   public static Builder alternatives(SpawnPoolEntryContainer.Builder<?>... pChildren) {
      return new Builder(pChildren);
   }

   public static <E> Builder alternatives(Collection<E> pChildrenSources, Function<E, SpawnPoolEntryContainer.Builder<?>> pToChildrenFunction) {
      return new Builder(pChildrenSources.stream().map(pToChildrenFunction::apply).toArray(SpawnPoolEntryContainer.Builder[]::new));
   }

   public static class Builder extends SpawnPoolEntryContainer.Builder<Builder> {
      private final List<SpawnPoolEntryContainer> entries = Lists.newArrayList();

      public Builder(SpawnPoolEntryContainer.Builder<?>... pChildren) {
         for(SpawnPoolEntryContainer.Builder<?> builder : pChildren) {
            this.entries.add(builder.build());
         }

      }

      protected Builder getThis() {
         return this;
      }

      public Builder otherwise(SpawnPoolEntryContainer.Builder<?> pChildBuilder) {
         this.entries.add(pChildBuilder.build());
         return this;
      }

      public SpawnPoolEntryContainer build() {
         return new AlternativesEntry(this.entries.toArray(new SpawnPoolEntryContainer[0]), this.getConditions());
      }
   }
}