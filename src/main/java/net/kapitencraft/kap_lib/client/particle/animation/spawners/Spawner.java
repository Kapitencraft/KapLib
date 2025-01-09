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

public abstract class Spawner {
    protected final ParticleOptions particle;

    protected Spawner(ParticleOptions particle) {
        this.particle = particle;
    }

    @ApiStatus.Internal
    @OnlyIn(Dist.CLIENT)
    public static Spawner fromNw(FriendlyByteBuf buf) {
        Spawner.Type<?> type = buf.readRegistryIdUnsafe(ExtraRegistries.SPAWN_ELEMENT_TYPES);
        return type.fromNw(buf, Minecraft.getInstance().level);
    }

    @ApiStatus.Internal
    public static <T extends Spawner> void toNw(FriendlyByteBuf buf, T element) {
        Spawner.Type<T> type = Objects.requireNonNull((Spawner.Type<T>) element.getType(), "element " + element + " does not declare type!");
        buf.writeRegistryIdUnsafe(ExtraRegistries.SPAWN_ELEMENT_TYPES, type);
        type.toNW(buf, element);
    }

    /**
     * ticks this spawner. spawn Particles using {@link ParticleSpawnSink#accept(ParticleOptions, Vec3) ParticleSpawnSink#accept(...)}
     * @param sink the particle spawn acceptor
     */
    public abstract void spawn(ParticleSpawnSink sink);

    /**
     * @return the type of this spawner. must be registered to the {@link net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys#SPAWN_ELEMENT_TYPES ExtraRegistryKeys#SPAWN_ELEMENT_TYPES}
     */
    public abstract @NotNull Type<? extends Spawner> getType();

    /**
     * a builder for the Spawner.<br>
     * required due to access of {@link ParticleAnimation.Builder#spawn(Builder) ParticleAnimation$Builder#spawn} taking a Builder
     */
    public static abstract class Builder<T extends Builder<T>> {
        protected ParticleOptions particle;

        public T setParticle(ParticleOptions particle) {
            this.particle = particle;
            return self();
        }

        private T self() {
            return (T) this;
        }

        public abstract Spawner build();
    }

    /**
     * the type of the spawner
     */
    public interface Type<T extends Spawner> {
        /**
         * use {@link net.kapitencraft.kap_lib.helpers.NetworkHelper#writeParticleOptions(FriendlyByteBuf, ParticleOptions) NetworkHelper#writeParticleOptions} for encrypting the particle options
         * @param buf the target network buffer
         * @param value the Spawner to encrypt
         */
        void toNW(FriendlyByteBuf buf, T value);

        /**
         * @param buf the ByteBuf to read the information from
         * @param level the level to read extra information from (like Entities)
         * @return a generated spawner from the ByteBuf
         */
        @OnlyIn(Dist.CLIENT)
        T fromNw(FriendlyByteBuf buf, ClientLevel level);
    }
}
