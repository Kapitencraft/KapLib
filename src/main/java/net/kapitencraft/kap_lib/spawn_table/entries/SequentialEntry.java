package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * A composite loot pool entry container that expands all its children in order until one of them fails.
 * This container succeeds if all children succeed.
 */
public class SequentialEntry extends CompositeEntryBase {
   public static final MapCodec<? extends SpawnPoolEntryContainer> CODEC = createCodec(SequentialEntry::new);

   public SequentialEntry(List<SpawnPoolEntryContainer> pChildren, List<LootItemCondition> pConditions) {
      super(pChildren, pConditions);
   }

   public SpawnPoolEntryType getType() {
      return SpawnPoolEntries.SEQUENCE.get();
   }

   /**
    * Compose the given children into one container.
    */
   protected ComposableEntryContainer compose(List<? extends ComposableEntryContainer> pEntries) {
      switch (pEntries.size()) {
         case 0:
            return ALWAYS_TRUE;
         case 1:
            return pEntries.get(0);
         case 2:
            return pEntries.get(0).and(pEntries.get(1));
         default:
            return (p_79819_, p_79820_) -> {
               for(ComposableEntryContainer composableentrycontainer : pEntries) {
                  if (!composableentrycontainer.expand(p_79819_, p_79820_)) {
                     return false;
                  }
               }

               return true;
            };
      }
   }

   public static Builder sequential(SpawnPoolEntryContainer.Builder<?>... pChildren) {
      return new Builder(pChildren);
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

      public Builder then(SpawnPoolEntryContainer.Builder<?> pChildBuilder) {
         this.entries.add(pChildBuilder.build());
         return this;
      }

      public SpawnPoolEntryContainer build() {
         return new SequentialEntry(this.entries, this.getConditions());
      }
   }
}