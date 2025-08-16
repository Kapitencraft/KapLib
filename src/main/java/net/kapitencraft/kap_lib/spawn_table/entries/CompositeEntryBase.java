package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.SpawnPoolEntry;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

/**
 * Base class for loot pool entry containers that delegate to one or more children.
 * The actual functionality is provided by composing the children into one composed container (see {@link #compose}).
 */
public abstract class CompositeEntryBase extends SpawnPoolEntryContainer {
   protected final List<SpawnPoolEntryContainer> children;
   private final ComposableEntryContainer composedChildren;

   protected CompositeEntryBase(List<SpawnPoolEntryContainer> pChildren, List<LootItemCondition> pConditions) {
      super(pConditions);
      this.children = pChildren;
      this.composedChildren = this.compose(pChildren);
   }

   public void validate(ValidationContext pValidationContext) {
      super.validate(pValidationContext);
      if (this.children.isEmpty()) {
         pValidationContext.reportProblem("Empty children list");
      }

      for(int i = 0; i < this.children.size(); ++i) {
         this.children.get(i).validate(pValidationContext.forChild(".entry[" + i + "]"));
      }

   }

   /**
    * Compose the given children into one container.
    */
   protected abstract ComposableEntryContainer compose(List<? extends ComposableEntryContainer> pEntries);

   /**
    * Expand this loot pool entry container by calling {@code entryConsumer} with any applicable entries
    * 
    * @return whether this loot pool entry container successfully expanded or not
    */
   public final boolean expand(SpawnContext pLootContext, Consumer<SpawnPoolEntry> pEntryConsumer) {
      return this.canRun(pLootContext) && this.composedChildren.expand(pLootContext, pEntryConsumer);
   }

   public static <T extends CompositeEntryBase> MapCodec<T> createCodec(CompositeEntryBase.CompositeEntryConstructor<T> factory) {
      return RecordCodecBuilder.mapCodec(
              p_338125_ -> p_338125_.group(SpawnPoolEntries.CODEC.listOf().optionalFieldOf("children", List.of()).forGetter(p_299120_ -> p_299120_.children))
                      .and(commonFields(p_338125_).t1())
                      .apply(p_338125_, factory::create)
      );
   }

   @FunctionalInterface
   public interface CompositeEntryConstructor<T extends CompositeEntryBase> {
      T create(List<SpawnPoolEntryContainer> pChildren, List<LootItemCondition> pConditions);
   }
}