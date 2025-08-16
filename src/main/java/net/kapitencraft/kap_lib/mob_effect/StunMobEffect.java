package net.kapitencraft.kap_lib.mob_effect;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
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

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity instanceof Player player) {
            TextHelper.setHotbarDisplay(player, Component.translatable("effect.stun.timer", MathHelper.shortRound(pLivingEntity.getEffect(this).duration / 20.)).withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity living, @NotNull AttributeMap p_19470_, int p_19471_) {
        super.removeAttributeModifiers(living, p_19470_, p_19471_);
        if (living instanceof Player player) {
            TextHelper.setHotbarDisplay(player, Component.translatable("effect.stun.released").withStyle(ChatFormatting.GREEN));
        }
    }
}
