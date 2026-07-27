package net.kapitencraft.kap_lib.attribute.mixin.classes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.kapitencraft.kap_lib.core.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
class LivingEntityMixin implements MixinSelfProvider<LivingEntity> {
    @Shadow @Final private static EntityDataAccessor<Boolean> DATA_EFFECT_AMBIENCE_ID;
    @Unique
    private static final List<Holder<Attribute>> GENERIC_ATTRIBUTES = List.of(
            ExtraAttributes.RANGED_DAMAGE,
            ExtraAttributes.DOUBLE_JUMP,
            ExtraAttributes.DODGE,
            ExtraAttributes.CRIT_DAMAGE,
            ExtraAttributes.FEROCITY,
            ExtraAttributes.ARMOR_SHREDDER,
            ExtraAttributes.BONUS_ATTACK_SPEED,
            ExtraAttributes.PROJECTILE_SPEED,
            ExtraAttributes.LIFE_STEAL,
            ExtraAttributes.STRENGTH,
            ExtraAttributes.VITALITY,
            ExtraAttributes.TRUE_DEFENCE,
            ExtraAttributes.MAGIC_DEFENCE
    );

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

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("TAIL"))
    private static AttributeSupplier.Builder addAllCommonAttributes(AttributeSupplier.Builder original) {
        GENERIC_ATTRIBUTES.forEach(original::add);
        return original;
    }
}
