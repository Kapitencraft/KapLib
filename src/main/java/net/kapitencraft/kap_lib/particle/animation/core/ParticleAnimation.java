package net.kapitencraft.kap_lib.particle.animation.core;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.EntityAddedTrigger;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTriggerInstance;
import net.kapitencraft.kap_lib.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPreset;
import net.kapitencraft.kap_lib.particle.animation.terminators.EntityRemovedTerminatorTrigger;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTriggerInstance;
import net.kapitencraft.kap_lib.particle.network.S2C.SendParticleAnimationPacket;
import net.minecraft.CrashReport;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * static data container for animations. created from presets or with a builder. use {@link ParticleAnimator} for dynamic information such as tick count
 */
public class ParticleAnimation {
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticleAnimation> STREAM_CODEC = ExtraStreamCodecs.composite(
            AnimationElement.STREAM_CODEC.apply(ByteBufCodecs.list()), ParticleAnimation::allElements,
            ParticleFinalizer.STREAM_CODEC, p -> p.finalizer,
            TerminationTrigger.STREAM_CODEC.apply(ByteBufCodecs.list()), ParticleAnimation::getTerminators,
            ActivationTrigger.STREAM_CODEC.apply(ByteBufCodecs.list()), ParticleAnimation::getTriggers,
            Spawner.STREAM_CODEC, p -> p.spawner,
            ByteBufCodecs.INT, p -> p.minSpawnDelay,
            ByteBufCodecs.INT, p -> p.maxSpawnDelay,
            ParticleAnimation::new
    );

    private final List<AnimationElement> elements;
    private final ParticleFinalizer finalizer;
    private final List<TerminationTriggerInstance> terminators;
    private final List<ActivationTriggerInstance> activationTriggers;
    private final Spawner spawner;
    public final int minSpawnDelay, maxSpawnDelay;

    private ParticleAnimation(ParticleAnimationBuilder builder, Map<String, Entity> context) {
        if (builder.minSpawnDelay > builder.maxSpawnDelay)
            throw new IllegalStateException("minimum spawn delay must be smaller than maximum spawn delay");
        if (builder.minSpawnDelay < -1 || builder.minSpawnDelay == 0)
            throw new IllegalStateException("minimum spawn delay must be above 0 or -1");
        this.elements = builder.elements.stream().map(b -> (AnimationElement) b.build(context)).toList();
        this.finalizer = Objects.requireNonNull(builder.finalizer.build(context), "animations must have a finalizer");
        this.spawner = Objects.requireNonNull(builder.spawner.build(context), "animations must have a spawner");
        this.terminators = builder.terminators;
        if (this.terminators.isEmpty()) throw new IllegalStateException("particle animation must have a terminator");
        this.maxSpawnDelay = builder.maxSpawnDelay;
        this.minSpawnDelay = builder.minSpawnDelay;
        this.activationTriggers = builder.activationTriggers;
    }

    public ParticleAnimation(List<AnimationElement> elements, ParticleFinalizer finalizer, List<TerminationTriggerInstance> terminators, List<ActivationTriggerInstance> activationTriggers, Spawner spawner, int minSpawnDelay, int maxSpawnDelay) {
        this.elements = elements;
        this.finalizer = finalizer;
        this.terminators = terminators;
        this.spawner = spawner;
        this.minSpawnDelay = minSpawnDelay;
        this.maxSpawnDelay = maxSpawnDelay;
        this.activationTriggers = activationTriggers;
    }

    public AnimationElement getElement(int elementIndex) {
        return elements.get(elementIndex);
    }

    public List<AnimationElement> allElements() {
        return elements;
    }

    public List<TerminationTriggerInstance> getTerminators() {
        return terminators;
    }

    public void spawnTick(ParticleSpawnSink sink) {
        this.spawner.spawn(sink);
    }

    public List<ActivationTriggerInstance> getTriggers() {
        return activationTriggers;
    }

    public void fillCrashReport(CrashReport report) {
        report.addCategory("Animation")
                .setDetail("Elements", this.elements)
                .setDetail("Particle Finalizer", this.finalizer)
                .setDetail("Terminator", this.terminators)
                .setDetail("Activation Triggers", this.activationTriggers)
                .setDetail("Spawner", this.spawner)
                .setDetail("minSpawnDelay", this.minSpawnDelay)
                .setDetail("maxSpawnDelay", this.maxSpawnDelay);
    }

    public static ParticleAnimationBuilder builder() {
        return new ParticleAnimationBuilder();
    }

    /**
     * creates a new Builder which starts when the given entity is added and ends when the given entity is removed
     */
    public static ParticleAnimationBuilder requireEntity(Entity target) {
        return builder()
                .activatedOn(EntityAddedTrigger.forEntity(target))
                .terminatedWhen(EntityRemovedTerminatorTrigger.create(target));
    }

    /**
     * particle animation builder. create using {@link #builder()}
     */
    public static class ParticleAnimationBuilder {

        private final List<AnimationElement.Builder<?>> elements = new ArrayList<>();
        private Spawner.SpawnerBuilder<?> spawner;
        private ParticleFinalizer.Builder<?> finalizer;
        private final List<TerminationTriggerInstance> terminators = new ArrayList<>();
        private int minSpawnDelay, maxSpawnDelay;
        private final List<ActivationTriggerInstance> activationTriggers = new ArrayList<>();

        private ParticleAnimationBuilder() {
        }

        /**
         * sets the spawner of this builder
         *
         * @param spawn the spawner to use
         */
        public ParticleAnimationBuilder spawn(Spawner.SpawnerBuilder<?> spawn) {
            spawner = spawn;
            Preconditions.checkNotNull(spawn.type(), "Spawner without Type detected!");
            return this;
        }

        /**
         * sets the minimum amount of ticks between particle spawns
         * use {@link #spawnTime(SpawnTime)}
         */
        private ParticleAnimationBuilder minSpawnTickTime(int minTickTime) {
            minSpawnDelay = minTickTime;
            return this;
        }

        /**
         * sets the maximum amount of ticks between particle spawns
         * use {@link #spawnTime(SpawnTime)}
         */
        private ParticleAnimationBuilder maxSpawnTickTime(int maxSpawnTickTime) {
            this.maxSpawnDelay = maxSpawnTickTime;
            return this;
        }

        public ParticleAnimationBuilder spawnTime(SpawnTime time) {
            return minSpawnTickTime(time.min).maxSpawnTickTime(time.max);
        }

        /**
         * sets the particles finalizer
         */
        public ParticleAnimationBuilder finalizes(ParticleFinalizer.Builder<?> finalizerBuilder) {
            this.finalizer = finalizerBuilder;
            Preconditions.checkNotNull(finalizerBuilder.type(), "Finalizer without type detected!");
            return this;
        }

        /**
         * sets the animation termination predicate
         */
        public ParticleAnimationBuilder terminatedWhen(TerminationTriggerInstance terminator) {
            Preconditions.checkNotNull(terminator.getTrigger(), "Terminator without type detected!");
            this.terminators.add(terminator);
            return this;
        }

        public ParticleAnimationBuilder activatedOn(ActivationTriggerInstance activationListener) {
            Preconditions.checkNotNull(activationListener.getTrigger(), "Activation Listener without trigger detected!");
            this.activationTriggers.add(activationListener);
            return this;
        }

        /**
         * adds a new animation element to this animation
         */
        public ParticleAnimationBuilder then(AnimationElement.Builder<?> builder) {
            elements.add(builder);
            return this;
        }

        @ApiStatus.Internal
        private ParticleAnimation build() {
            return new ParticleAnimation(this, Map.of());
        }

        /**
         * used to register the animation of this builder to the target players AnimationManager via network
         *
         * @param player the player to send the animation to
         */
        public void sendToPlayer(ServerPlayer player) {
            ParticleAnimation animation = this.build();
            ServerParticleAnimationManager.accept(animation);
            PacketDistributor.sendToPlayer(player, new SendParticleAnimationPacket(animation));
        }

        /**
         * used to register the animation of this builder to all players inside the given level
         */
        public void sendToAllPlayers() {
            ParticleAnimation animation = this.build();
            ServerParticleAnimationManager.accept(animation);
            PacketDistributor.sendToAllPlayers(new SendParticleAnimationPacket(animation));
        }

        /**
         * used to directly register the animation of this builder to the manager. only call clientside!
         */
        @OnlyIn(Dist.CLIENT)
        public void register() {
            ClientParticleAnimationManager.INSTANCE.accept(this.build());
        }
    }

    public static class SpawnTime {
        private final int min, max;

        public SpawnTime(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public static SpawnTime once() {
            return new SpawnTime(-1, -1);
        }

        public static SpawnTime absolute(int time) {
            return new SpawnTime(time, time);
        }

        public static SpawnTime range(int min, int max) {
            return new SpawnTime(min, max);
        }
    }

    @ApiStatus.Internal
    public void finalize(ParticleConfig config) {
        this.finalizer.finalize(config);
    }
}
