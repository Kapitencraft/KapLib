package net.kapitencraft.kap_lib.mob_effect.mixin.classes;

import net.kapitencraft.kap_lib.mob_effect.registry.ExtraMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntityExtension {

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> pEffect);

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "isImmobile", at = @At("HEAD"), cancellable = true)
    private void addStunEffect(CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(ExtraMobEffects.STUN)) cir.setReturnValue(true);
    }
}
