package net.kapitencraft.kap_lib.client.particle.animation.core;

import com.google.common.base.Preconditions;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.EntityAddedTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.TriggerInstance;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.EntityRemovedTerminatorTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTriggerInstance;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.S2C.SendParticleAnimationPacket;
import net.kapitencraft.kap_lib.client.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.CrashReport;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * static data container for animations. use {@link ParticleAnimator} for dynamic information such as tick count
 */
public class ParticleAnimation {
    private final AnimationElement[] elements;
    private final ParticleFinalizer finalizer;
    private final TerminationTriggerInstance terminator;
    private final TriggerInstance[] activationTriggers;
    private final Spawner spawner;
    public final int minSpawnDelay, maxSpawnDelay;

    private ParticleAnimation(Builder builder) {
        if (builder.minSpawnDelay > builder.maxSpawnDelay) throw new IllegalStateException("minimum spawn delay must be smaller than maximum spawn delay");
        if (builder.minSpawnDelay < -1 || builder.minSpawnDelay == 0) throw new IllegalStateException("minimum spawn delay must be above 0 or -1");
        this.elements = builder.elements.toArray(new AnimationElement[0]);
        this.finalizer = Objects.requireNonNull(builder.finalizer, "animations must have a finalizer");
        this.spawner = Objects.requireNonNull(builder.spawner, "animations must have a spawner");
        this.terminator = Objects.requireNonNull(builder.terminator, "animations must have a terminator");
        this.maxSpawnDelay = builder.maxSpawnDelay;
        this.minSpawnDelay = builder.minSpawnDelay;
        this.activationTriggers = Objects.requireNonNull(builder.activationTriggers.toArray(TriggerInstance[]::new));
    }

    private ParticleAnimation(AnimationElement[] elements, ParticleFinalizer finalizer, TerminationTriggerInstance terminator, Spawner spawner, int minSpawnDelay, int maxSpawnDelay, TriggerInstance[] activationTriggers) {
        this.elements = elements;
        this.finalizer = finalizer;
        this.terminator = terminator;
        this.spawner = spawner;
        this.minSpawnDelay = minSpawnDelay;
        this.maxSpawnDelay = maxSpawnDelay;
        this.activationTriggers = activationTriggers;
    }

    public AnimationElement getElement(int elementIndex) {
        return elements[elementIndex];
    }

    public AnimationElement[] allElements() {
        return elements;
    }

    public TerminationTriggerInstance getTerminator() {
        return terminator;
    }

    public void spawnTick(ParticleSpawnSink sink) {
        this.spawner.spawn(sink);
    }

    public TriggerInstance[] getTriggers() {
        return activationTriggers;
    }

    public void fillCrashReport(CrashReport report) {
        report.addCategory("Animation")
                .setDetail("Elements", Arrays.toString(this.elements))
                .setDetail("Particle Finalizer", this.finalizer)
                .setDetail("Terminator", this.terminator)
                .setDetail("Activation Triggers", Arrays.toString(this.activationTriggers))
                .setDetail("Spawner", this.spawner)
                .setDetail("minSpawnDelay", this.minSpawnDelay)
                .setDetail("maxSpawnDelay", this.maxSpawnDelay);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * creates a new Builder which starts when the given entity is added and ends when the given entity is removed
     */
    public static Builder requireEntity(Entity target) {
        return builder()
                .activatedOn(EntityAddedTrigger.forEntity(target))
                .terminatedWhen(EntityRemovedTerminatorTrigger.create(target));
    }

    /**
     * particle animation builder. create using {@link #builder()}
     */
    public static class Builder {
        private final List<AnimationElement> elements = new ArrayList<>();
        private Spawner spawner;
        private ParticleFinalizer finalizer;
        private TerminationTriggerInstance terminator;
        private int minSpawnDelay, maxSpawnDelay;
        private final List<TriggerInstance> activationTriggers = new ArrayList<>();

        private Builder() {}

        /**
         * sets the spawner of this builder
         * @param spawn the spawner to use
         */
        public Builder spawn(Spawner.Builder spawn) {
            spawner = spawn.build();
            Preconditions.checkNotNull(spawner.getType(), "Spawner without Type detected!");
            return this;
        }

        /**
         * sets the minimum amount of ticks between particle spawns
         * use {@link #spawnTime(SpawnTime)}
         */
        private Builder minSpawnTickTime(int minTickTime) {
            minSpawnDelay = minTickTime;
            return this;
        }

        /**
         * sets the maximum amount of ticks between particle spawns
         * use {@link #spawnTime(SpawnTime)}
         */
        private Builder maxSpawnTickTime(int maxSpawnTickTime) {
            this.maxSpawnDelay = maxSpawnTickTime;
            return this;
        }

        public Builder spawnTime(SpawnTime time) {
            return minSpawnTickTime(time.min).maxSpawnTickTime(time.max);
        }

        /**
         * sets the particles finalizer
         */
        public Builder finalizes(ParticleFinalizer.Builder finalizerBuilder) {
            this.finalizer = finalizerBuilder.build();
            Preconditions.checkNotNull(this.finalizer.getType(), "Finalizer without type detected!");
            return this;
        }

        /**
         * sets the animation termination predicate
         */
        public Builder terminatedWhen(TerminationTriggerInstance terminator) {
            this.terminator = terminator;
            Preconditions.checkNotNull(this.terminator.getTrigger(), "Terminator without type detected!");
            return this;
        }

        public Builder activatedOn(TriggerInstance activationListener) {
            this.activationTriggers.add(activationListener);
            Preconditions.checkNotNull(activationListener.getTrigger(), "Activation Listener without trigger detected!");
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
            LibClient.animations.accept(this.build());
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

    /**
     * used to write information to a network stream
     */
    @ApiStatus.Internal
    public void toNW(FriendlyByteBuf buf) {
        buf.writeInt(this.minSpawnDelay);
        buf.writeInt(this.maxSpawnDelay);
        NetworkHelper.writeArray(buf, elements, AnimationElement::toNw);
        Spawner.toNw(buf, this.spawner);
        ParticleFinalizer.toNw(buf, this.finalizer);
        buf.writeRegistryIdUnsafe(ExtraRegistries.TERMINATION_TRIGGERS, this.terminator.getTrigger());
        TerminationTrigger.writeToNw(buf, this.terminator);
        NetworkHelper.writeArray(buf, this.activationTriggers, ActivationTrigger::writeToNw);
    }

    /**
     * used to read a particle information from the network stream
     */
    @ApiStatus.Internal
    public static ParticleAnimation fromNw(FriendlyByteBuf buf) {
        int minSpawnDelay = buf.readInt();
        int maxSpawnDelay = buf.readInt();
        AnimationElement[] elements = NetworkHelper.readArray(buf, AnimationElement[]::new, AnimationElement::fromNw);

        Spawner spawner = Spawner.fromNw(buf);
        ParticleFinalizer finalizer = ParticleFinalizer.fromNw(buf);
        TerminationTriggerInstance terminator = TerminationTrigger.readFromNw(buf);

        TriggerInstance[] triggers = NetworkHelper.readArray(buf, TriggerInstance[]::new, ActivationTrigger::readFromNw);

        return new ParticleAnimation(elements, finalizer, terminator, spawner, minSpawnDelay, maxSpawnDelay, triggers);
    }

    @ApiStatus.Internal
    public void finalize(ParticleConfig config) {
        this.finalizer.finalize(config);
    }
}
