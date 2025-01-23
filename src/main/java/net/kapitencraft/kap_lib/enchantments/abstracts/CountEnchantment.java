package net.kapitencraft.kap_lib.enchantments.abstracts;

import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public interface CountEnchantment extends ExtendedCalculationEnchantment, IWeaponEnchantment {
    @ApiStatus.Internal
    default String mapName() {
        return Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey((Enchantment) this), "unknown enchantment").toString();
    }

    CountType countType();

    int getCountAmount(int level);

    @Override
    default double execute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, double damageAmount, DamageSource source) {
        CompoundTag attackerTag = attacker.getPersistentData();
        HashMap<UUID, Integer> map = !attackerTag.getCompound(this.mapName()).isEmpty() ? IOHelper.getHashMapTag(attackerTag.getCompound(this.mapName())) : new HashMap<>();
        if (!map.containsKey(attacked.getUUID())) {
            map.put(attacked.getUUID(), 1);
        }
        int i = map.get(attacked.getUUID());
        if (i >= this.getCountAmount(level)) {
            i = 1;
            if (this.countType() == CountType.NORMAL) {
                damageAmount = this.mainExecute(level, enchanted, attacker, attacked, damageAmount, 0, source);
            }
        } else {
            i++;
            if (this.countType() == CountType.EXCEPT) {
                damageAmount = this.mainExecute(level, enchanted, attacker, attacked, damageAmount, i, source);
            }
        }
        map.put(attacked.getUUID(), i);
        attackerTag.put(this.mapName(), IOHelper.putHashMapTag(map));
        return damageAmount;
    }

    double mainExecute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, double damageAmount, int curHit, DamageSource source);

    enum CountType {
        NORMAL,
        EXCEPT;
    }
}
