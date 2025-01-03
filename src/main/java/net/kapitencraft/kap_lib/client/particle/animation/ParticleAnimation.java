package net.kapitencraft.kap_lib.client.particle.animation;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.S2C.SendParticleAnimationPacket;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.client.particle.animation.modifiers.AnimationElement;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.AnimationTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.ParticleFinalizer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * static data container for animations. use {@link ParticleAnimator} for dynamic information such as tick count
 */
public class ParticleAnimation {
    private final AnimationElement[] elements;
    private final ParticleFinalizer finalizer;
    private final AnimationTerminator terminator;
    private final Spawner spawner;
    public final int minSpawnDelay, maxSpawnDelay;

    private ParticleAnimation(Builder builder) {
        if (builder.minSpawnDelay > builder.maxSpawnDelay) throw new IllegalStateException("minimum spawn delay must be smaller than maximum spawn delay");
        if (builder.minSpawnDelay <= 0) throw new IllegalStateException("minimum spawn delay must be above 0");
        this.elements = builder.elements.toArray(new AnimationElement[0]);
        this.finalizer = Objects.requireNonNull(builder.finalizer, "animations must have a finalizer");
        this.spawner = Objects.requireNonNull(builder.spawner, "animations must have a spawner");
        this.terminator = Objects.requireNonNull(builder.terminator, "animations must have a terminator");
        this.maxSpawnDelay = builder.maxSpawnDelay;
        this.minSpawnDelay = builder.minSpawnDelay;
    }

    private ParticleAnimation(AnimationElement[] elements, ParticleFinalizer finalizer, AnimationTerminator terminator, Spawner spawner, int minSpawnDelay, int maxSpawnDelay) {
        this.elements = elements;
        this.finalizer = finalizer;
        this.terminator = terminator;
        this.spawner = spawner;
        this.minSpawnDelay = minSpawnDelay;
        this.maxSpawnDelay = maxSpawnDelay;
    }

    public static Builder builder() {
        return new Builder();
    }

    public AnimationElement getElement(int elementIndex) {
        return elements[elementIndex];
    }

    public AnimationElement[] allElements() {
        return elements;
    }

    public boolean terminated(ParticleAnimator animator) {
        return terminator.shouldTerminate(animator);
    }

    public void spawnTick(ParticleSpawnSink sink) {
        this.spawner.spawn(sink);
    }

    /**
     * particle animation builder. create using {@link #builder()}
     */
    public static class Builder {
        private final List<AnimationElement> elements = new ArrayList<>();
        private Spawner spawner;
        private ParticleFinalizer finalizer;
        private AnimationTerminator terminator;
        private int minSpawnDelay, maxSpawnDelay;

        private Builder() {}

        /**
         * sets the spawner of this builder
         * @param spawn the spawner to use
         */
        public Builder spawn(Spawner.Builder<?> spawn) {
            spawner = spawn.build();
            return this;
        }

        /**
         * sets the minimum amount of ticks between particle spawns
         */
        public Builder minSpawnTickTime(int minTickTime) {
            minSpawnDelay = minTickTime;
            return this;
        }

        /**
         * sets the maximum amount of ticks between particle spawns
         */
        public Builder maxSpawnTickTime(int maxSpawnTickTime) {
            this.maxSpawnDelay = maxSpawnTickTime;
            return this;
        }

        /**
         * sets the particles finalizer
         */
        public Builder finalizes(ParticleFinalizer.Builder finalizerBuilder) {
            this.finalizer = finalizerBuilder.build();
            return this;
        }

        /**
         * sets the animation termination predicate
         */
        public Builder terminatedWhen(AnimationTerminator.Builder terminator) {
            this.terminator = terminator.build();
            return this;
        }

        /**
         * adds a new animation element to this animation
         */
        public Builder then(AnimationElement.Builder builder) {
            elements.add(builder.build());
            return this;
        }

        @ApiStatus.Internal
        private ParticleAnimation build() {
            return new ParticleAnimation(this);
        }

        /**
         * used to register the animation of this builder to the target players AnimationManager via network
         * @param player the player to send the animation to
         */
        public void sendToPlayer(ServerPlayer player) {
            ModMessages.sendToClientPlayer(new SendParticleAnimationPacket(this.build()), player);
        }

        /**
         * used to register the animation of this builder to all players inside the given level
         * @param level the level
         */
        public void sendToAllPlayers(ServerLevel level) {
            ModMessages.sendToAllConnectedPlayers(sp -> new SendParticleAnimationPacket(this.build()), level);
        }

        /**
         * used to directly register the animation of this builder to the manager. only call clientside!
         */
        @OnlyIn(Dist.CLIENT)
        public void register() {
            LibClient.particleManager.accept(this.build());
        }
    }

    /**
     * used to write information to a network stream
     */
    @ApiStatus.Internal
    public void toNW(FriendlyByteBuf buf) {
        buf.writeInt(this.minSpawnDelay);
        buf.writeInt(this.maxSpawnDelay);
        buf.writeInt(elements.length);
        for (AnimationElement e : elements) {
            AnimationElement.toNw(buf, e);
        }
        Spawner.toNw(buf, this.spawner);
        ParticleFinalizer.toNw(buf, this.finalizer);
        AnimationTerminator.toNw(buf, this.terminator);
    }

    /**
     * used to read a particle information from the network stream
     */
    @ApiStatus.Internal
    public static ParticleAnimation fromNw(FriendlyByteBuf buf) {
        int minSpawnDelay = buf.readInt();
        int maxSpawnDelay = buf.readInt();
        AnimationElement[] elements = new AnimationElement[buf.readInt()];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = AnimationElement.fromNw(buf);
        }
        Spawner spawner = Spawner.fromNw(buf);
        ParticleFinalizer finalizer = ParticleFinalizer.fromNw(buf);
        AnimationTerminator terminator = AnimationTerminator.fromNw(buf);

        return new ParticleAnimation(elements, finalizer, terminator, spawner, minSpawnDelay, maxSpawnDelay);
    }

    @ApiStatus.Internal
    public void finalize(ParticleConfig config) {
        this.finalizer.finalize(config);
    }
}
