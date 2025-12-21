package net.kapitencraft.kap_lib.mob_effect;

import net.kapitencraft.kap_lib.core.helpers.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class StunMobEffect extends MobEffect {
    public StunMobEffect() {
        super(MobEffectCategory.HARMFUL, -16777216);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity instanceof Player player) {
            TextHelper.setHotbarDisplay(player, Component.translatable("effect.stun.timer").withStyle(ChatFormatting.RED));
        }
        return true;
    }
}
