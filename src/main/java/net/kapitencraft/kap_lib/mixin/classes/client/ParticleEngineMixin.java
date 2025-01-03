package net.kapitencraft.kap_lib.mixin.classes.client;

import net.kapitencraft.kap_lib.client.LibClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Shadow protected ClientLevel level;

    @Shadow @Final private RandomSource random;

    @Inject(method = "tick", at = @At("HEAD"))
    private void addAnimationTick(CallbackInfo ci) {
        this.level.getProfiler().push("KapLib particle animation");
        LibClient.particleManager.tick(this.random);
        this.level.getProfiler().pop();
    }
}
