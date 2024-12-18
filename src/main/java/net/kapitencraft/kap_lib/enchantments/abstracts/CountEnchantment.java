package net.kapitencraft.kap_lib.enchantments.abstracts;

import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.HashMap;
import java.util.UUID;

public abstract class CountEnchantment extends ExtendedCalculationEnchantment implements IWeaponEnchantment {
    private final String mapName;
    private final CountType type;
    protected CountEnchantment(Rarity p_44676_, EnchantmentCategory category, EquipmentSlot[] slots, String mapName, CountType type, CalculationType calculationType, ProcessPriority priority) {
        super(p_44676_, category, slots, calculationType, priority);
        this.mapName = mapName;
        this.type = type;
    }

    protected abstract int getCountAmount(int level);

    @Override
    public double execute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, double damageAmount, DamageSource source) {
        CompoundTag attackerTag = attacker.getPersistentData();
        HashMap<UUID, Integer> map = !attackerTag.getCompound(this.mapName).isEmpty() ? IOHelper.getHashMapTag(attackerTag.getCompound(this.mapName)) : new HashMap<>();
        if (!map.containsKey(attacked.getUUID())) {
            map.put(attacked.getUUID(), 1);
        }
        int i = map.get(attacked.getUUID());
        if (i >= this.getCountAmount(level)) {
            i = 1;
            if (this.type == CountType.NORMAL) {
                damageAmount = this.mainExecute(level, enchanted, attacker, attacked, damageAmount, 0, source);
            }
        } else {
            i++;
            if (this.type == CountType.EXCEPT) {
                damageAmount = this.mainExecute(level, enchanted, attacker, attacked, damageAmount, i, source);
            }
        }
        map.put(attacked.getUUID(), i);
        attackerTag.put(this.mapName, IOHelper.putHashMapTag(map));
        return damageAmount;
    }

    protected abstract double mainExecute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, double damageAmount, int curHit, DamageSource source);

    protected enum CountType {
        NORMAL,
        EXCEPT;
    }
}
