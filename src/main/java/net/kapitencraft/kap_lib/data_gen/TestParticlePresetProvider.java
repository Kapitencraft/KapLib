package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.finalizers.SetLifeTimeFinalizer;
import net.kapitencraft.kap_lib.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.particle.data.ParticlePresetProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.PackOutput;

public class TestParticlePresetProvider extends ParticlePresetProvider {

    public TestParticlePresetProvider(PackOutput output) {
        super(output);
    }

    @Override
    public void register() {
        this.add(KapLibMod.res("aura"), ParticleAnimation.builder()
                .spawnTime(ParticleAnimation.SpawnTime.absolute(1))
                .finalizes(SetLifeTimeFinalizer.builder().resetAge().lifeTime(20))
                .spawn(RingSpawner.entityWithBBSize("target", 0.51f, 1.8f)
                        .setParticle(ParticleTypes.FLAME)
                        .rotPerTick(5)
                        .heightPerTick(.02f)
                )
                .terminatedWhen(TimedTerminator.ticks(600))
        );
    }
}
