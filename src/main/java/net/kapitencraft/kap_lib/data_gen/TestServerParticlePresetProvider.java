package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.elements.MoveAwayElement;
import net.kapitencraft.kap_lib.particle.animation.finalizers.RemoveParticleFinalizer;
import net.kapitencraft.kap_lib.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.particle.animation.target.pos.PositionTarget;
import net.kapitencraft.kap_lib.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.particle.data.ParticlePresetProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.PackOutput;

public class TestServerParticlePresetProvider extends ParticlePresetProvider {
    public TestServerParticlePresetProvider(PackOutput output) {
        super(output, PackOutput.Target.DATA_PACK);
    }

    @Override
    public void register() {
        add(LibConstants.res("rotation"), ParticleAnimation.builder()
                .spawnTime(ParticleAnimation.SpawnTime.absolute(1))
                .finalizes(RemoveParticleFinalizer.builder())
                .spawn(RingSpawner
                        .noHeight()
                        .axis(Direction.Axis.Y)
                        .radius(.1f)
                        .rotPerTick(1)
                        .setTarget(PositionTarget.fixed("pos"))
                        .setParticle(ParticleTypes.FLAME)
                ).terminatedWhen(TimedTerminator.ticks(600))
                .then(MoveAwayElement.builder().speed(.01f).time(20).target(PositionTarget.fixed("pos")))
        );
    }
}
