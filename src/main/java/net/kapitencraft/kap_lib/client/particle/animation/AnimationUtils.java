package net.kapitencraft.kap_lib.client.particle.animation;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.GroupSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.LineSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface AnimationUtils {

    static ParticleAnimation.Builder star(int tips, ParticleOptions starType, @Nullable ParticleOptions ringType, float spacing, float radius, PositionTarget origin) {
        float angleBetweenTips = 360f / tips * 2;

        GroupSpawner.Builder spawner = GroupSpawner.builder();

        for (int i = 0; i < tips; i++) {
            spawner.addSpawner(LineSpawner.builder()
                    .start(PositionTarget.relative(origin, point(angleBetweenTips * i).scale(radius)))
                    .end(PositionTarget.relative(origin, point(angleBetweenTips * (i+1)).scale(radius)))
                    .spacing(spacing)
                    .setParticle(starType)
            );
        }
        if (ringType != null) {
            spawner.addSpawner(RingSpawner.fullCircle(360)
                    .axis(Direction.Axis.Y)
                    .radius(radius)
                    .setTarget(origin)
                    .setParticle(ringType)
            );
        }

        return ParticleAnimation.builder()
                .spawn(spawner)
                .spawnTime(ParticleAnimation.SpawnTime.once());
    }

    private static Vec3 point(float angle) {
        angle *= Mth.DEG_TO_RAD;
        return new Vec3(Mth.sin(angle), 0, Mth.cos(angle));
    }
}
