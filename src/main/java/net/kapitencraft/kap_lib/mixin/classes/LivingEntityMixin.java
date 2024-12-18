package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.kapitencraft.kap_lib.requirements.RequirementType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IForgeLivingEntity {

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot pSlot);

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "updateFallFlying", at = @At(value = "HEAD"), cancellable = true)
    private void checkRequirements(CallbackInfo ci) {
        if (!level().isClientSide() && !RequirementManager.instance.meetsRequirements(RequirementType.ITEM, getItemBySlot(EquipmentSlot.CHEST).getItem(), self())) {
            setSharedFlag(7, false);
            ci.cancel();
        }
    }

    /**
     * @reason armor-shredder attribute
     * @author Kapitencraft
     */
    @Inject(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtArmor(Lnet/minecraft/world/damagesource/DamageSource;F)V", shift = At.Shift.AFTER), cancellable = true)
    public void getDamageAfterArmorAbsorb(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        double armorShredValue = source.getEntity() instanceof LivingEntity living ? AttributeHelper.getSaveAttributeValue(ExtraAttributes.ARMOR_SHREDDER.get(), living) : 0;
        double armorValue = Math.max(0, getArmorValue(source) - armorShredValue);
        cir.setReturnValue(MathHelper.calculateDamage(damage, (float) armorValue, (float) self().getAttributeValue(Attributes.ARMOR_TOUGHNESS)));
    }

    private double getArmorValue(DamageSource source) {
        if (source.getMsgId().equals("true_damage")) {
            return AttributeHelper.getSaveAttributeValue(ExtraAttributes.TRUE_DEFENCE.get(), self());
        } else {
            return AttributeHelper.getSaveAttributeValue(Attributes.ARMOR, self());
        }
    }
}
