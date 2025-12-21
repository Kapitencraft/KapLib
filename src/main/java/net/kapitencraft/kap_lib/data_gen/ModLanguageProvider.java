package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.datagen.EnglishLanguageProvider;
import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.kapitencraft.kap_lib.mana.ManaAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.data.PackOutput;

public class ModLanguageProvider extends EnglishLanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, LibConstants.MOD_ID);
    }

    @Override
    protected void addTranslations() {
        addDeathMessage("ferocity", "%1$s was ferociously murdered by %2$s");
        addAttribute(ExtraAttributes.MAGIC_DAMAGE, ChatFormatting.RED);
        addAttribute(ExtraAttributes.ARMOR_SHREDDER, null);
        addAttribute(ExtraAttributes.PROJECTILE_SPEED, null);
        addAttribute(ExtraAttributes.BONUS_ATTACK_SPEED, ChatFormatting.YELLOW);
        addAttribute(ExtraAttributes.CRIT_DAMAGE, ChatFormatting.DARK_BLUE);
        addAttribute(ExtraAttributes.DODGE, ChatFormatting.BLACK);
        addAttribute(ExtraAttributes.DOUBLE_JUMP, null);
        addAttribute(ExtraAttributes.DRAW_SPEED, ChatFormatting.GOLD);
        addAttribute(ExtraAttributes.FEROCITY, ChatFormatting.WHITE);
        addAttribute(ExtraAttributes.FISHING_SPEED, ChatFormatting.AQUA);
        addAttribute(ExtraAttributes.LIFE_STEAL, ChatFormatting.DARK_RED);
        addAttribute(ManaAttributes.MANA_COST, null);
        addAttribute(ManaAttributes.MANA_REGEN, ChatFormatting.BLUE);
        addAttribute(ManaAttributes.MAX_MANA, ChatFormatting.DARK_AQUA);
        addAttribute(ExtraAttributes.STRENGTH, ChatFormatting.DARK_RED);
    }
}
