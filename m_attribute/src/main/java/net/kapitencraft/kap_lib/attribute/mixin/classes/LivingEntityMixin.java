package net.kapitencraft.kap_lib.attribute.mixin.classes;

import net.kapitencraft.kap_lib.core.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
class LivingEntityMixin implements MixinSelfProvider<LivingEntity> {

    /**
     * @reason armor-shredder attribute
     * @author Kapitencraft
     */
    @Inject(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtArmor(Lnet/minecraft/world/damagesource/DamageSource;F)V", shift = At.Shift.AFTER), cancellable = true)
    private void getDamageAfterArmorAbsorb(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        double armorShredValue = source.getEntity() instanceof LivingEntity living ? AttributeHelper.getSaveAttributeValue(ExtraAttributes.ARMOR_SHREDDER, living) : 0;
        double armorValue = Math.max(0, getArmorValue(source) - armorShredValue);
        cir.setReturnValue(MathHelper.calculateDamage(damage, (float) armorValue, (float) self().getAttributeValue(Attributes.ARMOR_TOUGHNESS)));
    }

    @Unique
    private double getArmorValue(DamageSource source) {
        if (source.getMsgId().equals("true_damage")) {
            return AttributeHelper.getSaveAttributeValue(ExtraAttributes.TRUE_DEFENCE, self());
        } else {
            return AttributeHelper.getSaveAttributeValue(Attributes.ARMOR, self());
        }
    }

    @Inject(method = "hurt", at = @At(value = "RETURN", ordinal = 6))
    private void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (source.getEntity() != null && source.getEntity() instanceof LivingEntity living) {
            double attackSpeed = AttributeHelper.getSaveAttributeValue(ExtraAttributes.BONUS_ATTACK_SPEED, living);
            if (attackSpeed > 0) {
                self().invulnerableTime = (int) (20 - (attackSpeed * 0.15));
            }
        }
    }
}
