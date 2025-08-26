package net.kapitencraft.kap_lib.spawn_table;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnPoolEntryContainer;
import net.kapitencraft.kap_lib.spawn_table.functions.core.FunctionUserBuilder;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SpawnPool {
   final SpawnPoolEntryContainer[] entries;
   final LootItemCondition[] conditions;
   private final Predicate<LootContext> compositeCondition;
   final SpawnEntityFunction[] functions;
   private final BiFunction<Entity, SpawnContext, Entity> compositeFunction;
   NumberProvider rolls;
   NumberProvider bonusRolls;

   SpawnPool(SpawnPoolEntryContainer[] pEntries, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions, NumberProvider pRolls, NumberProvider pBonusRolls, @org.jetbrains.annotations.Nullable String name) {
      this.name = name;
      this.entries = pEntries;
      this.conditions = pConditions;
      this.compositeCondition = LootItemConditions.andConditions(pConditions);
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
      for(int i = 0; i < this.conditions.length; ++i) {
         this.conditions[i].validate(pContext.forChild(".condition[" + i + "]"));
      }

      for(int j = 0; j < this.functions.length; ++j) {
         this.functions[j].validate(pContext.forChild(".functions[" + j + "]"));
      }

      for(int k = 0; k < this.entries.length; ++k) {
         this.entries[k].validate(pContext.forChild(".entries[" + k + "]"));
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
   private final String name;
   @org.jetbrains.annotations.Nullable
   public String getName() { return this.name; }
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
            return new SpawnPool(this.entries.toArray(new SpawnPoolEntryContainer[0]), this.conditions.toArray(new LootItemCondition[0]), this.functions.toArray(new SpawnEntityFunction[0]), this.rolls, this.bonusRolls, name);
         }
      }
   }

   public static class Serializer implements JsonDeserializer<SpawnPool>, JsonSerializer<SpawnPool> {
      public SpawnPool deserialize(JsonElement pJson, Type pTypeOfT, JsonDeserializationContext pContext) throws JsonParseException {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(pJson, "loot pool");
         SpawnPoolEntryContainer[] containers = GsonHelper.getAsObject(jsonobject, "entries", pContext, SpawnPoolEntryContainer[].class);
         LootItemCondition[] alootitemcondition = GsonHelper.getAsObject(jsonobject, "conditions", new LootItemCondition[0], pContext, LootItemCondition[].class);
         SpawnEntityFunction[] functions = GsonHelper.getAsObject(jsonobject, "functions", new SpawnEntityFunction[0], pContext, SpawnEntityFunction[].class);
         NumberProvider rolls = GsonHelper.getAsObject(jsonobject, "rolls", pContext, NumberProvider.class);
         NumberProvider bonusRolls = GsonHelper.getAsObject(jsonobject, "bonus_rolls", ConstantValue.exactly(0.0F), pContext, NumberProvider.class);
         return new SpawnPool(containers, alootitemcondition, functions, rolls, bonusRolls, GsonHelper.getAsString(jsonobject, "name"));
      }

      public JsonElement serialize(SpawnPool pSrc, Type pTypeOfSrc, JsonSerializationContext pContext) {
         JsonObject jsonobject = new JsonObject();
         if (pSrc.name != null && !pSrc.name.startsWith("custom#"))
            jsonobject.add("name", pContext.serialize(pSrc.name));
         jsonobject.add("rolls", pContext.serialize(pSrc.rolls));
         jsonobject.add("bonus_rolls", pContext.serialize(pSrc.bonusRolls));
         jsonobject.add("entries", pContext.serialize(pSrc.entries));
         if (!ArrayUtils.isEmpty(pSrc.conditions)) {
            jsonobject.add("conditions", pContext.serialize(pSrc.conditions));
         }

         if (!ArrayUtils.isEmpty(pSrc.functions)) {
            jsonobject.add("functions", pContext.serialize(pSrc.functions));
         }

         return jsonobject;
      }
   }
}
