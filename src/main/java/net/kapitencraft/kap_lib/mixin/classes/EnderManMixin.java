package net.kapitencraft.kap_lib.mixin.classes;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public abstract class EnderManMixin extends Monster implements NeutralMob {

    @Shadow protected abstract boolean teleport();

    protected EnderManMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) { //DUMMY
        super(p_33002_, p_33003_);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void addArrowHittable(DamageSource source, float f, CallbackInfoReturnable<Boolean> cir) {
        Entity projectile = source.getDirectEntity();
        if (projectile != null && projectile.getPersistentData().contains("HitsEnderMan")) {
            boolean flag = super.hurt(source, f);
            if (!this.level().isClientSide() && !(source.getEntity() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
                this.teleport();
            }
            cir.setReturnValue(flag);
        }
    }
}
