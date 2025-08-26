package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.helpers.LootTableHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayDeque;
import java.util.Deque;

public class SetArmorFunction extends SpawnEntityConditionalFunction {
    private final LootTable[] armorItems;
    private final float[] armorDropChances;

    protected SetArmorFunction(LootItemCondition[] pPredicates, LootTable[] armorItems, float[] armorDropChances) {
        super(pPredicates);
        this.armorItems = armorItems;
        this.armorDropChances = armorDropChances;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof Mob mob) {
            EquipmentSlot[] armor = MiscHelper.ARMOR_EQUIPMENT;
            for (int i = 0; i < armor.length; i++) {
                EquipmentSlot slot = armor[i];
                if (armorItems[i] != null) {
                    armorItems[i].getRandomItems(pContext,
                            stack -> pEntity.setItemSlot(slot, stack)
                    );
                }
                if (armorDropChances != null) {
                    mob.setDropChance(slot, armorDropChances[i]);
                }
            }
        } else logWrongType("Mob", pEntity);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_ARMOR.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetArmorFunction> {

        @Override
        public void serialize(JsonObject pJson, SetArmorFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.add("armorItems", pSerializationContext.serialize(pFunction.armorItems));
            if (pFunction.armorDropChances != null) pJson.add("armorDropChances", pSerializationContext.serialize(pFunction.armorDropChances));
        }

        @Override
        public SetArmorFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            LootTable[] armorItems = pObject.has("armorItems") ? pDeserializationContext.deserialize(pObject.get("armorItems"), LootTable[].class) : new LootTable[4];
            float[] armorChances = pObject.has("armorChances") ? pDeserializationContext.deserialize(pObject.get("armorChances"), float[].class) : null;
            return new SetArmorFunction(pConditions, armorItems, armorChances);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final LootTable[] items = new LootTable[4];
        private float[] dropChances = null;

        public Builder withItem(EquipmentSlot slot, LootPool.Builder entry) {
            if (!slot.isArmor()) throw new IllegalArgumentException("can not set armor item of non-armor slot");
            items[slot.getIndex()] = LootTable.lootTable().withPool(entry.setRolls(ConstantValue.exactly(1)).setBonusRolls(ConstantValue.exactly(1))).build();
            return this;
        }

        public Builder withItem(EquipmentSlot slot, ItemLike item) {
            return withItem(slot, LootPool.lootPool().add(LootItem.lootTableItem(item)));
        }

        public Builder withDropChance(EquipmentSlot slot, float chance) {
            if (!slot.isArmor()) throw new IllegalArgumentException("can not set armor item of non-armor slot");
            if (dropChances == null) dropChances = new float[4];
            dropChances[slot.getIndex()] = chance;
            return this;
        }

        public Builder withItemAndDropChance(EquipmentSlot slot, LootPool.Builder entry, float chance) {
            return withItem(slot, entry).withDropChance(slot, chance);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetArmorFunction(getConditions(), items, dropChances);
        }
    }
}
