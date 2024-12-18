package net.kapitencraft.kap_lib.enchantments.abstracts;

import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class ModProtectionEnchantment extends Enchantment implements IArmorEnchantment {
    private final Type type;

    protected ModProtectionEnchantment(Rarity p_44676_, Type type) {
        super(p_44676_, EnchantmentCategory.ARMOR, DEFAULT_SLOTS);
        this.type = type;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getDamageProtection(int level, @NotNull DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_ARMOR)) return 0;
        if (source.getMsgId().equals("true_damage") && this.type == Type.TRUE) {
            return level;
        } else if (source.is(ExtraTags.DamageTypes.MAGIC) && this.type == Type.MAGIC) {
            return 2 * level;
        }
        return 0;
    }


    protected enum Type {
        TRUE,
        MAGIC
    }
}