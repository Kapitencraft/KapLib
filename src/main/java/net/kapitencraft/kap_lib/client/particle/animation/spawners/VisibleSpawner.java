package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class VisibleSpawner implements Spawner {
    protected final ParticleOptions particle;

    protected VisibleSpawner(ParticleOptions particle) {
        this.particle = Objects.requireNonNull(particle, "Spawner Particle null!");
    }

    /**
     * a builder for the Spawner.<br>
     * required due to access of {@link ParticleAnimation.Builder#spawn(Spawner.Builder) ParticleAnimation$Builder#spawn} taking a Builder
     */
    public static abstract class Builder<T extends Builder<T>> implements Spawner.Builder {
        protected ParticleOptions particle;

        public T setParticle(ParticleOptions particle) {
            this.particle = particle;
            return self();
        }

        private T self() {
            return (T) this;
        }
    }
}
