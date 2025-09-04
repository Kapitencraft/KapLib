package net.kapitencraft.kap_lib.spawn_table.functions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SetArmorFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetArmorFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.either(ResourceKey.codec(Registries.LOOT_TABLE), LootTable.DIRECT_CODEC).listOf().fieldOf("armorItems").forGetter(f -> f.armorItems),
            Codec.FLOAT.listOf().optionalFieldOf("dropChances", List.of()).forGetter(f -> f.armorDropChances)
    ).and(commonFields(i).t1()).apply(i, SetArmorFunction::new));

    private final List<Either<ResourceKey<LootTable>, LootTable>> armorItems;
    private final List<Float> armorDropChances;

    protected SetArmorFunction(List<Either<ResourceKey<LootTable>, LootTable>> armorItems, List<Float> armorDropChances, List<LootItemCondition> pPredicates) {
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
                if (armorItems.get(i) != null) {
                    LootTable table = armorItems.get(i).map(
                            key -> pContext.getResolver().get(Registries.LOOT_TABLE, key).map(Holder::value).orElse(LootTable.EMPTY),
                            Function.identity()
                    );
                    table.getRandomItems(pContext,
                            stack -> mob.setItemSlot(slot, stack)
                    );
                }
                if (armorDropChances != null) {
                    mob.setDropChance(slot, armorDropChances.get(i));
                }
            }
        } else logWrongType("Mob", pEntity);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.SET_ARMOR.get();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final List<Either<ResourceKey<LootTable>, LootTable>> items = new ArrayList<>();
        private List<Float> dropChances = null;

        public Builder withItem(EquipmentSlot slot, LootPool.Builder entry) {
            if (!slot.isArmor()) throw new IllegalArgumentException("can not set armor item of non-armor slot");
            items.set(slot.getIndex(), Either.right(LootTable.lootTable().withPool(entry.setRolls(ConstantValue.exactly(1)).setBonusRolls(ConstantValue.exactly(1))).build()));
            return this;
        }

        public Builder withItem(EquipmentSlot slot, ResourceKey<LootTable> key) {
            if (!slot.isArmor()) throw new IllegalArgumentException("can not set armor item of non-armor slot");
            if (items.get(slot.getIndex()) != null) throw new IllegalStateException("duplicate slot registration!");
            items.set(slot.getIndex(), Either.left(key));
            return this;
        }

        public Builder withItem(EquipmentSlot slot, ItemLike item) {
            return withItem(slot, LootPool.lootPool().add(LootItem.lootTableItem(item)));
        }

        public Builder withDropChance(EquipmentSlot slot, float chance) {
            if (!slot.isArmor()) throw new IllegalArgumentException("can not set armor item of non-armor slot");
            if (dropChances == null) dropChances = new ArrayList<>();
            dropChances.set(slot.getIndex(), chance);
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
            return new SetArmorFunction(items, dropChances, getConditions());
        }
    }
}
