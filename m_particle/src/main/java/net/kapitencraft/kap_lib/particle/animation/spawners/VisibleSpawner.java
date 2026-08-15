package net.kapitencraft.kap_lib.particle.animation.spawners;

import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.minecraft.core.particles.ParticleOptions;

import java.util.Objects;

public abstract class VisibleSpawner implements Spawner {
    protected final String texture;

    protected VisibleSpawner(String texture) {
        this.texture = Objects.requireNonNull(texture, "Spawner Texture null!");
    }

    /**
     * a builder for the Spawner.<br>
     * required due to access of {@link ParticleAnimation.ParticleAnimationBuilder#spawn(SpawnerBuilder) ParticleAnimation$Builder#spawn} taking a Builder
     * @param <T> subtype of the builder
     */
    public static abstract class Builder<B extends Builder<B, T>, T extends Spawner> implements SpawnerBuilder<T> {
        protected String texture;

        public B setTexture(String texture) {
            this.texture = texture;
            return self();
        }

        private B self() {
            return (B) this;
        }
    }
}
