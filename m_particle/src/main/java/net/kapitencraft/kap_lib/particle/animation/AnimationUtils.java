package net.kapitencraft.kap_lib.particle.animation;

import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.spawners.GroupSpawner;
import net.kapitencraft.kap_lib.particle.animation.spawners.LineSpawner;
import net.kapitencraft.kap_lib.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.particle.animation.target.pos.PositionTarget;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Useful for quick animations
 * Feel free to suggest more animations on the discord or on GitHub
 */
public interface AnimationUtils {

    /**
     * generates a flat star spawner for animation
     *
     * @param tips        the amount of tips the star has
     * @param starTexture what particle the star should use
     * @param ringTexture if there should be a ring around the star and if so, which particle should be used
     * @param spacing     the distance between each particle spawn.
     * @param radius      the radius of the star
     * @param origin      the origin (center) position of the star
     * @return the animation builder setup to spawn a star
     */
    static ParticleAnimation.ParticleAnimationBuilder star(int tips, String starTexture, @Nullable String ringTexture, float spacing, float radius, PositionTarget.Builder<?> origin) {
        float angleBetweenTips = 360f / tips * 2;

        GroupSpawner.Builder spawner = GroupSpawner.builder();

        for (int i = 0; i < tips; i++) {
            spawner.addSpawner(LineSpawner.builder()
                    .start(PositionTarget.relative(origin, point(angleBetweenTips * i).scale(radius)))
                    .end(PositionTarget.relative(origin, point(angleBetweenTips * (i + 1)).scale(radius)))
                    .spacing(spacing)
                    .setTexture(starTexture)
            );
        }
        if (ringTexture != null) {
            spawner.addSpawner(RingSpawner.fullCircle(360)
                    .axis(Direction.Axis.Y)
                    .radius(radius)
                    .setTarget(origin)
                    .setTexture(ringTexture)
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
