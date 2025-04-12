package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

/**
 * A loot pool entry that always generates a given item.
 */
public class SpawnEntity extends SpawnPoolSingletonContainer {
   final EntityType<?> entityType;

   SpawnEntity(EntityType<?> pEntity, int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions) {
      super(pWeight, pQuality, pConditions, pFunctions);
      this.entityType = pEntity;
   }

   public SpawnPoolEntryType getType() {
      return SpawnPoolEntries.ENTITY.get();
   }

   /**
    * Generate the loot entities of this entry.
    * Contrary to the method name this method does not always generate one stack, it can also generate zero or multiple
    * stacks.
    */
   public void createEntity(Consumer<Entity> pEntityConsumer, SpawnContext pLootContext) {
      pEntityConsumer.accept(this.entityType.create(pLootContext.getLevel()));
   }

   public static Builder<?> spawnTableEntity(EntityType<?> pEntity) {
      return simpleBuilder((p_79583_, p_79584_, p_79585_, p_79586_) ->
              new SpawnEntity(pEntity, p_79583_, p_79584_, p_79585_, p_79586_)
      );
   }

   public static class Serializer extends SpawnPoolSingletonContainer.Serializer<SpawnEntity> {
      public void serializeCustom(JsonObject pObject, SpawnEntity pContainer, JsonSerializationContext pConditions) {
         super.serializeCustom(pObject, pContainer, pConditions);
         ResourceLocation resourcelocation = ForgeRegistries.ENTITY_TYPES.getKey(pContainer.entityType);
         if (resourcelocation == null) {
            throw new IllegalArgumentException("Can't serialize unknown item " + pContainer.entityType);
         } else {
            pObject.addProperty("name", resourcelocation.toString());
         }
      }

      protected SpawnEntity deserialize(JsonObject pObject, JsonDeserializationContext pContext, int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions) {
         EntityType<?> item = JsonHelper.convertToRegistryElement(pObject, "name", ForgeRegistries.ENTITY_TYPES);
         return new SpawnEntity(item, pWeight, pQuality, pConditions, pFunctions);
      }
   }
}