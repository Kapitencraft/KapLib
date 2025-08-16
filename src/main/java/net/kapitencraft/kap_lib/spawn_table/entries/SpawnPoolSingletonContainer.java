package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnPoolEntry;
import net.kapitencraft.kap_lib.spawn_table.functions.core.FunctionUserBuilder;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * A LootPoolEntryContainer that expands into a single LootPoolEntry.
 */
public abstract class SpawnPoolSingletonContainer extends SpawnPoolEntryContainer {
   public static final int DEFAULT_WEIGHT = 1;
   public static final int DEFAULT_QUALITY = 0;
   /** The weight of the entry. */
   protected final int weight;
   /** The quality of the entry. */
   protected final int quality;
   /** Functions that are ran on the entry. */
   protected final List<SpawnEntityFunction> functions;
   final BiFunction<Entity, SpawnContext, Entity> compositeFunction;
   private final SpawnPoolEntry entry = new SpawnPoolSingletonContainer.EntryBase() {
      /**
       * Generate the loot stacks of this entry.
       * Contrary to the method name this method does not always generate one stack, it can also generate zero or
       * multiple stacks.
       */
      public void createEntity(Consumer<Entity> entitySink, SpawnContext context) {
         SpawnPoolSingletonContainer.this.createEntity(SpawnEntityFunction.decorate(SpawnPoolSingletonContainer.this.compositeFunction, entitySink, context), context);
      }
   };

   protected SpawnPoolSingletonContainer(int pWeight, int pQuality, List<LootItemCondition> pConditions, List<SpawnEntityFunction> pFunctions) {
      super(pConditions);
      this.weight = pWeight;
      this.quality = pQuality;
      this.functions = pFunctions;
      this.compositeFunction = SpawnEntityFunctions.compose(pFunctions);
   }

   public void validate(ValidationContext pValidationContext) {
      super.validate(pValidationContext);

      for(int i = 0; i < this.functions.size(); ++i) {
         this.functions.get(i).validate(pValidationContext.forChild(".functions[" + i + "]"));
      }

   }

   /**
    * Generate the loot entities of this entry.
    * Contrary to the method name this method does not always generate one entity, it can also generate zero or multiple
    * entities.
    */
   protected abstract void createEntity(Consumer<Entity> pEntityConsumer, SpawnContext pLootContext);

   /**
    * Expand this loot pool entry container by calling {@code entryConsumer} with any applicable entries
    * 
    * @return whether this loot pool entry container successfully expanded or not
    */
   public boolean expand(SpawnContext pLootContext, Consumer<SpawnPoolEntry> pEntryConsumer) {
      if (this.canRun(pLootContext)) {
         pEntryConsumer.accept(this.entry);
         return true;
      } else {
         return false;
      }
   }

   public static SpawnPoolSingletonContainer.Builder<?> simpleBuilder(SpawnPoolSingletonContainer.EntryConstructor pEntryBuilder) {
      return new SpawnPoolSingletonContainer.DummyBuilder(pEntryBuilder);
   }

   public abstract static class Builder<T extends SpawnPoolSingletonContainer.Builder<T>> extends SpawnPoolEntryContainer.Builder<T> implements FunctionUserBuilder<T> {
      protected int weight = 1;
      protected int quality = 0;
      private final List<SpawnEntityFunction> functions = Lists.newArrayList();

      public T apply(SpawnEntityFunction.Builder pFunctionBuilder) {
         this.functions.add(pFunctionBuilder.build());
         return this.getThis();
      }

      /**
       * Creates an array from the functions list
       */
      protected List<SpawnEntityFunction> getFunctions() {
         return this.functions;
      }

      public T setWeight(int pWeight) {
         this.weight = pWeight;
         return this.getThis();
      }

      public T setQuality(int pQuality) {
         this.quality = pQuality;
         return this.getThis();
      }
   }

   static class DummyBuilder extends SpawnPoolSingletonContainer.Builder<SpawnPoolSingletonContainer.DummyBuilder> {
      private final SpawnPoolSingletonContainer.EntryConstructor constructor;

      public DummyBuilder(SpawnPoolSingletonContainer.EntryConstructor pConstructor) {
         this.constructor = pConstructor;
      }

      protected SpawnPoolSingletonContainer.DummyBuilder getThis() {
         return this;
      }

      public SpawnPoolEntryContainer build() {
         return this.constructor.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
      }
   }

   protected abstract class EntryBase implements SpawnPoolEntry {
      /**
       * Gets the effective weight based on the loot entry's weight and quality multiplied by looter's luck.
       */
      public int getWeight(float pLuck) {
         return Math.max(Mth.floor((float) SpawnPoolSingletonContainer.this.weight + (float) SpawnPoolSingletonContainer.this.quality * pLuck), 0);
      }
   }

   @FunctionalInterface
   protected interface EntryConstructor {
      SpawnPoolSingletonContainer build(int pWeight, int pQuality, List<LootItemCondition> pConditions, List<SpawnEntityFunction> pFunctions);
   }

   protected static <T extends SpawnPoolSingletonContainer> Products.P4<RecordCodecBuilder.Mu<T>, Integer, Integer, List<LootItemCondition>, List<SpawnEntityFunction>> singletonFields(
           RecordCodecBuilder.Instance<T> instance
   ) {
      return instance.group(
                      Codec.INT.optionalFieldOf("weight", 1).forGetter(p_299262_ -> p_299262_.weight),
                      Codec.INT.optionalFieldOf("quality", 0).forGetter(p_299272_ -> p_299272_.quality)
              )
              .and(commonFields(instance).t1())
              .and(SpawnEntityFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(p_298225_ -> p_298225_.functions));
   }
}