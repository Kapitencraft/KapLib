package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    @Shadow private double baseDamage;

    protected AbstractArrowMixin(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;position()Lnet/minecraft/world/phys/Vec3;", ordinal = 1), cancellable = true)
    private void fixRotationLock(CallbackInfo ci) {
        if (this.getDeltaMovement().equals(Vec3.ZERO))
            ci.cancel();
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private void changeDamage(EntityType<? extends Projectile> entityType, LivingEntity owner, Level level, ItemStack pickupItemStack, ItemStack firedFromWeapon, CallbackInfo ci) {
        this.baseDamage = owner.getAttributeValue(ExtraAttributes.RANGED_DAMAGE);
    }
}
