package net.kapitencraft.kap_lib.mixin.classes.client;

import net.kapitencraft.kap_lib.util.particle_help.animation.IAnimatable;
import net.kapitencraft.kap_lib.util.particle_help.animation.ParticleAnimator;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityClientMixin implements IAnimatable {
    @Unique
    private final ParticleAnimator animator = new ParticleAnimator(self());

    @Unique
    private Entity self() {
        return (Entity) (Object) this;
    }

    @Override
    public ParticleAnimator getAnimator() {
        return animator;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void addAnimatorTick(CallbackInfo ci) {
        animator.tick();
    }
}
