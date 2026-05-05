package net.kapitencraft.kap_lib.spawn_table;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnPoolEntryContainer;
import net.kapitencraft.kap_lib.spawn_table.functions.core.FunctionUserBuilder;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SpawnPool {
   public static final Codec<SpawnPool> CODEC = RecordCodecBuilder.create(inst ->
           inst.group(
                   SpawnPoolEntries.CODEC.listOf().fieldOf("entries").forGetter(p_297995_ -> p_297995_.entries),
                   LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(p_297992_ -> p_297992_.conditions),
                   SpawnEntityFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(p_297994_ -> p_297994_.functions),
                   NumberProviders.CODEC.fieldOf("rolls").forGetter(p_297993_ -> p_297993_.rolls),
                   NumberProviders.CODEC.fieldOf("bonus_rolls").orElse(ConstantValue.exactly(0.0F)).forGetter(p_297997_ -> p_297997_.bonusRolls),
                   Codec.STRING.optionalFieldOf("name").forGetter(pool -> java.util.Optional.ofNullable(pool.name).filter(name -> !name.startsWith("custom#")))
           ).apply(inst, SpawnPool::new)
   );

   final List<SpawnPoolEntryContainer> entries;
   final List<LootItemCondition> conditions;
   private final Predicate<LootContext> compositeCondition;
   final List<SpawnEntityFunction> functions;
   private final BiFunction<Entity, SpawnContext, Entity> compositeFunction;
   NumberProvider rolls;
   NumberProvider bonusRolls;

   SpawnPool(List<SpawnPoolEntryContainer> pEntries, List<LootItemCondition> pConditions, List<SpawnEntityFunction> pFunctions, NumberProvider pRolls, NumberProvider pBonusRolls, Optional<String> name) {
      this.name = name.orElse(null);
      this.entries = pEntries;
      this.conditions = pConditions;
      this.compositeCondition = Util.allOf(pConditions);
      this.functions = pFunctions;
      this.compositeFunction = SpawnEntityFunctions.compose(pFunctions);
      this.rolls = pRolls;
      this.bonusRolls = pBonusRolls;
   }

   private void addRandomEntity(Consumer<Entity> pEntityConsumer, SpawnContext pContext) {
      RandomSource randomsource = pContext.getRandom();
      List<SpawnPoolEntry> list = Lists.newArrayList();
      MutableInt mutableint = new MutableInt();

      for(SpawnPoolEntryContainer entryContainer : this.entries) {
         entryContainer.expand(pContext, (p_79048_) -> {
            int k = p_79048_.getWeight(pContext.getLuck());
            if (k > 0) {
               list.add(p_79048_);
               mutableint.add(k);
            }

         });
      }

      int i = list.size();
      if (mutableint.intValue() != 0 && i != 0) {
         if (i == 1) {
            list.get(0).createEntity(pEntityConsumer, pContext);
         } else {
            int j = randomsource.nextInt(mutableint.intValue());

            for(SpawnPoolEntry entry : list) {
               j -= entry.getWeight(pContext.getLuck());
               if (j < 0) {
                  entry.createEntity(pEntityConsumer, pContext);
                  return;
               }
            }

         }
      }
   }

   /**
    * Generate the random items from this LootPool to the given {@code stackConsumer}.
    * This first checks this pool's conditions, generating nothing if they do not match.
    * Then the random items are generated based on the {@link LootPoolEntry LootPoolEntries} in this pool according to
    * the rolls and bonusRolls, applying any loot functions.
    */
   public void addRandomEntities(Consumer<Entity> pStackConsumer, SpawnContext pLootContext) {
      if (this.compositeCondition.test(pLootContext)) {
         Consumer<Entity> consumer = SpawnEntityFunction.decorate(this.compositeFunction, pStackConsumer, pLootContext);
         int i = this.rolls.getInt(pLootContext) + Mth.floor(this.bonusRolls.getFloat(pLootContext) * pLootContext.getLuck());

         for(int j = 0; j < i; ++j) {
            this.addRandomEntity(consumer.andThen(pLootContext.getLevel()::addFreshEntity), pLootContext);
         }

      }
   }

   /**
    * Validate this LootPool according to the given context.
    */
   public void validate(ValidationContext pContext) {
      for(int i = 0; i < this.conditions.size(); ++i) {
         this.conditions.get(i).validate(pContext.forChild(".condition[" + i + "]"));
      }

      for(int j = 0; j < this.functions.size(); ++j) {
         this.functions.get(j).validate(pContext.forChild(".functions[" + j + "]"));
      }

      for(int k = 0; k < this.entries.size(); ++k) {
         this.entries.get(k).validate(pContext.forChild(".entries[" + k + "]"));
      }

      this.rolls.validate(pContext.forChild(".rolls"));
      this.bonusRolls.validate(pContext.forChild(".bonusRolls"));
   }

   private boolean isFrozen = false;
   public void freeze() { this.isFrozen = true; }
   public boolean isFrozen(){ return this.isFrozen; }
   private void checkFrozen() {
      if (this.isFrozen())
         throw new RuntimeException("Attempted to modify LootPool after being frozen!");
   }
   @org.jetbrains.annotations.Nullable
   private String name;
   @org.jetbrains.annotations.Nullable
   public String getName() { return this.name; }
   public void setName(String name) {
      this.name = name;
   }

   public NumberProvider getRolls()      { return this.rolls; }
   public NumberProvider getBonusRolls() { return this.bonusRolls; }
   public void setRolls     (NumberProvider v){ checkFrozen(); this.rolls = v; }
   public void setBonusRolls(NumberProvider v){ checkFrozen(); this.bonusRolls = v; }

   public static SpawnPool.Builder spawnPool(String name) {
      return new SpawnPool.Builder(name);
   }

   public static class Builder implements FunctionUserBuilder<Builder>, ConditionUserBuilder<SpawnPool.Builder> {
      private final List<SpawnPoolEntryContainer> entries = Lists.newArrayList();
      private final List<LootItemCondition> conditions = Lists.newArrayList();
      private final List<SpawnEntityFunction> functions = Lists.newArrayList();
      private NumberProvider rolls = ConstantValue.exactly(1.0F);
      private NumberProvider bonusRolls = ConstantValue.exactly(0.0F);
      private final String name;

       public Builder(String name) {
           this.name = name;
       }

       public SpawnPool.Builder setRolls(NumberProvider pRolls) {
         this.rolls = pRolls;
         return this;
      }

      public SpawnPool.Builder unwrap() {
         return this;
      }

      public SpawnPool.Builder setBonusRolls(NumberProvider pBonusRolls) {
         this.bonusRolls = pBonusRolls;
         return this;
      }

      public SpawnPool.Builder add(SpawnPoolEntryContainer.Builder<?> pEntriesBuilder) {
         this.entries.add(pEntriesBuilder.build());
         return this;
      }

      public SpawnPool.@NotNull Builder when(LootItemCondition.Builder pConditionBuilder) {
         this.conditions.add(pConditionBuilder.build());
         return this;
      }

      public SpawnPool.Builder apply(SpawnEntityFunction.Builder pFunctionBuilder) {
         this.functions.add(pFunctionBuilder.build());
         return this;
      }

      public SpawnPool build() {
         if (this.rolls == null) {
            throw new IllegalArgumentException("Rolls not set");
         } else {
            return new SpawnPool(this.entries, this.conditions, this.functions, this.rolls, this.bonusRolls, Optional.ofNullable(name));
         }
      }
   }
}
