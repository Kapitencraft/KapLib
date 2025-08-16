package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * A composite loot pool entry container that expands all its children in order.
 * This container always succeeds.
 */
public class EntryGroup extends CompositeEntryBase {
   public static final MapCodec<? extends SpawnPoolEntryContainer> CODEC = createCodec(EntryGroup::new);

   public EntryGroup(List<SpawnPoolEntryContainer> pChildren, List<LootItemCondition> pConditions) {
      super(pChildren, pConditions);
   }

   public SpawnPoolEntryType getType() {
      return SpawnPoolEntries.GROUP.get();
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
            ComposableEntryContainer container = pEntries.get(0);
            ComposableEntryContainer container1 = pEntries.get(1);
            return (p_79556_, p_79557_) -> {
               container.expand(p_79556_, p_79557_);
               container1.expand(p_79556_, p_79557_);
               return true;
            };
         default:
            return (p_79562_, p_79563_) -> {
               for(ComposableEntryContainer container2 : pEntries) {
                  container2.expand(p_79562_, p_79563_);
               }

               return true;
            };
      }
   }

   public static Builder list(SpawnPoolEntryContainer.Builder<?>... pChildren) {
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

      public Builder append(SpawnPoolEntryContainer.Builder<?> pChildBuilder) {
         this.entries.add(pChildBuilder.build());
         return this;
      }

      public SpawnPoolEntryContainer build() {
         return new EntryGroup(this.entries, this.getConditions());
      }
   }
}