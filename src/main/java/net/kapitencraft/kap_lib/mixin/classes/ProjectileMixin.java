package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {

    public ProjectileMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @ModifyVariable(method = "shootFromRotation", at = @At(value = "HEAD"), ordinal = 3, argsOnly = true)
    private float in(float i, Entity entity, float pX, float pY, float pZ, float pVelocity, float pInaccuracy) {
        if (entity instanceof LivingEntity living) { //TODO fix client & server vec issues
            return (float) (i * (1 + AttributeHelper.getSaveAttributeValue(ExtraAttributes.PROJECTILE_SPEED.get(), living) / 100));
        }
        return i;
    }

    @Inject(method = "lerpMotion", at = @At(value = "INVOKE", target = "Ljava/lang/Math;sqrt(D)D"), cancellable = true)
    private void fixRotationLock(double pX, double pY, double pZ, CallbackInfo ci) {
        if (pX == 0 && pY == 0 && pZ == 0) ci.cancel();
    }
}