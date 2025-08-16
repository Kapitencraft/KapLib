package net.kapitencraft.kap_lib.enchantments.abstracts;

import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.kapitencraft.kap_lib.requirements.type.RequirementType;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public interface ExtendedCalculationEnchantment extends ModEnchantment {

    @NotNull
    CalculationType type();

    @NotNull
    ProcessPriority priority();

    static Map<ExtendedCalculationEnchantment, Integer> getAllEnchantments(ItemEnchantments enchantments) {
        Map<ExtendedCalculationEnchantment, Integer> map = new HashMap<>();
        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            if (enchantment instanceof ExtendedCalculationEnchantment extended) {
                map.put(extended, stack.getAllEnchantments().get(extended));
            }
        }
        return map;
    }

    static float runWithPriority(ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, float damage, MiscHelper.DamageType type, DamageSource source) {
        Map<ExtendedCalculationEnchantment, Integer> enchantmentIntegerMap = getAllEnchantments(enchanted);
        for (ProcessPriority priority : ProcessPriority.values()) {
            for (ExtendedCalculationEnchantment enchantment : enchantmentIntegerMap.keySet()) {
                if (enchantment.priority() == priority && RequirementManager.instance.meetsRequirements(RequirementType.ENCHANTMENT, (Enchantment) enchantment, attacker)) {
                    damage = enchantment.tryExecute(enchantmentIntegerMap.get(enchantment), enchanted, attacker, attacked, damage, type, source);
                }
            }
        }
        return damage;
    }

    default float tryExecute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, float damage, MiscHelper.DamageType type, DamageSource source) {
        if (this.type().contains(type)) {
            float attackStrengthScale = 1;
            if (attacker instanceof Player p) {
                attackStrengthScale = p.getAttackStrengthScale(0);
            }
            return this.execute(level, enchanted, attacker, attacked, damage, source, attackStrengthScale);
        }
        return damage;
    }

    float execute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, float damage, DamageSource source, float attackStrengthScale);

    enum CalculationType {
        ONLY_MAGIC(MiscHelper.DamageType.MAGIC),
        NOT_MAGIC(MiscHelper.DamageType.MELEE, MiscHelper.DamageType.RANGED),
        ONLY_MELEE(MiscHelper.DamageType.MELEE),
        ONLY_RANGED(MiscHelper.DamageType.RANGED),
        ALL(MiscHelper.DamageType.MAGIC, MiscHelper.DamageType.RANGED, MiscHelper.DamageType.MELEE),
        ALL_RANGED(MiscHelper.DamageType.RANGED, MiscHelper.DamageType.MAGIC);

        private final MiscHelper.DamageType[] types;

        CalculationType(MiscHelper.DamageType... types) {
            this.types = types;
        }

        public boolean contains(MiscHelper.DamageType type) {
            for (MiscHelper.DamageType type1 : this.types) {
                if (type1 == type) {
                    return true;
                }
            }
            return false;
        }
    }

    public enum ProcessPriority {

        /**
         * used for changing the damage
         */
        HIGHEST,
        HIGH,
        MEDIUM,
        LOW,

        /**
         * used for special effects, like applying potions, attribute-modifiers etc
         */
        LOWEST;
    }
}
