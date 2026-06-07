package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.animation.AnimationUtils;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.elements.RotateElement;
import net.kapitencraft.kap_lib.particle.animation.finalizers.RemoveParticleFinalizer;
import net.kapitencraft.kap_lib.particle.animation.finalizers.SetLifeTimeFinalizer;
import net.kapitencraft.kap_lib.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.particle.animation.target.pos.PositionTarget;
import net.kapitencraft.kap_lib.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.particle.data.ParticlePresetProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.PackOutput;

public class TestClientParticlePresetProvider extends ParticlePresetProvider {

    public TestClientParticlePresetProvider(PackOutput output) {
        super(output, PackOutput.Target.RESOURCE_PACK);
    }

    @Override
    public void register() {
        this.add(LibConstants.res("aura"), ParticleAnimation.builder()
                .spawnTime(ParticleAnimation.SpawnTime.absolute(1))
                .finalizes(SetLifeTimeFinalizer.builder().resetAge().lifeTime(20))
                .spawn(RingSpawner.entityWithBBSize("target", 0.51f, 1.8f)
                        .setParticle(ParticleTypes.FLAME)
                        .rotPerTick(5)
                        .heightPerTick(.02f)
                )
                .terminatedWhen(TimedTerminator.ticks(600))
        );
        this.add(LibConstants.res("star"), AnimationUtils.star(
                5, ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.FLAME, .25f, 5f, PositionTarget.fixed("origin")
                ).terminatedWhen(TimedTerminator.ticks(600))
                .finalizes(RemoveParticleFinalizer.builder())
                .then(RotateElement.builder()
                        .angle(1)
                        .axis(Direction.Axis.Y)
                        .pivot(PositionTarget.fixed("origin"))
                        .duration(600)
                )
        );
    }
}
