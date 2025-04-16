package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.data_gen.abst.EnglishLanguageProvider;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.data.PackOutput;

public class ModLanguageProvider extends EnglishLanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, KapLibMod.MOD_ID);
    }

    @Override
    protected void addTranslations() {
        addDeathMessage("ferocity", "%1$s was ferociously murdered by %2$s");
        addAttribute(ExtraAttributes.ABILITY_DAMAGE, ChatFormatting.RED);
        addAttribute(ExtraAttributes.ARMOR_SHREDDER, null);
        addAttribute(ExtraAttributes.PROJECTILE_SPEED, null);
        addAttribute(ExtraAttributes.ARROW_COUNT, null);
        addAttribute(ExtraAttributes.BONUS_ATTACK_SPEED, ChatFormatting.YELLOW);
        addAttribute(ExtraAttributes.CRIT_DAMAGE, ChatFormatting.DARK_BLUE);
        addAttribute(ExtraAttributes.DODGE, ChatFormatting.BLACK);
        addAttribute(ExtraAttributes.DOUBLE_JUMP, null);
        addAttribute(ExtraAttributes.DRAW_SPEED, ChatFormatting.GOLD);
        addAttribute(ExtraAttributes.FEROCITY, ChatFormatting.WHITE);
        addAttribute(ExtraAttributes.FISHING_SPEED, ChatFormatting.AQUA);
        addAttribute(ExtraAttributes.INTELLIGENCE, ChatFormatting.DARK_PURPLE);
        addAttribute(ExtraAttributes.LIVE_STEAL, ChatFormatting.DARK_RED);
        addAttribute(ExtraAttributes.MANA, null);
        addAttribute(ExtraAttributes.MANA_COST, null);
        addAttribute(ExtraAttributes.MANA_REGEN, ChatFormatting.BLUE);
        addAttribute(ExtraAttributes.MAX_MANA, ChatFormatting.DARK_AQUA);
        addAttribute(ExtraAttributes.MINING_SPEED, null);
        addAttribute(ExtraAttributes.STRENGTH, ChatFormatting.DARK_RED);
    }
}
