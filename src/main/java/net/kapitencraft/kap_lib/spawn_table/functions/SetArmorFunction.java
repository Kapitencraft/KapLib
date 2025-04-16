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

//TODO fix StackOverflow
public class SetArmorFunction extends SpawnEntityConditionalFunction {
    private final LootPool[] armorItems;
    private final float[] armorDropChances;

    protected SetArmorFunction(LootItemCondition[] pPredicates, LootPool[] armorItems, float[] armorDropChances) {
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
                    armorItems[i].addRandomItems(
                            stack -> pEntity.setItemSlot(slot, stack), pContext);
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
            ThreadLocal<Deque<ForgeHooks.LootTableContext>> local = ForgeHooks.lootContext;
            if (local.get() == null) local.set(new ArrayDeque<>());
            local.get().add(new ForgeHooks.LootTableContext(KapLibMod.res("spawn_table/function/set_armor"), true));
            if (ForgeHooks.lootContext.get() == null) ForgeHooks.lootContext.set(new ArrayDeque<>());
            LootPool[] armorItems = pObject.has("armorItems") ? pDeserializationContext.deserialize(pObject.get("armorItems"), LootPool[].class) : new LootPool[4];
            float[] armorChances = pObject.has("armorChances") ? pDeserializationContext.deserialize(pObject.get("armorChances"), float[].class) : null;
            return new SetArmorFunction(pConditions, armorItems, armorChances);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final LootPool[] items = new LootPool[4];
        private float[] dropChances = null;

        public Builder withItem(EquipmentSlot slot, LootPool.Builder entry) {
            if (!slot.isArmor()) throw new IllegalArgumentException("can not set armor item of non-armor slot");
            items[slot.getIndex()] = entry.setRolls(ConstantValue.exactly(1)).setBonusRolls(ConstantValue.exactly(1)).build();
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
