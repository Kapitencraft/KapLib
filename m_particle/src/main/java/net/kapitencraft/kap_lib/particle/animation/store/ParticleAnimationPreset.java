package net.kapitencraft.kap_lib.particle.animation.store;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTriggerInstance;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTriggerInstance;

import java.util.List;
import java.util.stream.Collectors;

public record ParticleAnimationPreset(
        List<AnimationElement.Builder<?>> elements,
        ParticleFinalizer.Builder<?> finalizer,
        List<TerminationTriggerInstance> terminators,
        List<ActivationTriggerInstance> activationTriggers,
        Spawner.SpawnerBuilder<?> spawner,
        int minSpawnDelay, int maxSpawnDelay
) {
    public static final Codec<ParticleAnimationPreset> CODEC = RecordCodecBuilder.create(i -> i.group(
            AnimationElement.CODEC.listOf().fieldOf("elements").forGetter(ParticleAnimationPreset::elements),
            ParticleFinalizer.CODEC.fieldOf("finalizer").forGetter(ParticleAnimationPreset::finalizer),
            TerminationTrigger.CODEC.listOf().fieldOf("terminators").forGetter(ParticleAnimationPreset::terminators),
            ActivationTrigger.CODEC.listOf().fieldOf("activator").forGetter(ParticleAnimationPreset::activationTriggers),
            Spawner.CODEC.fieldOf("spawner").forGetter(ParticleAnimationPreset::spawner),
            Codec.INT.fieldOf("min_delay").forGetter(ParticleAnimationPreset::maxSpawnDelay),
            Codec.INT.fieldOf("max_delay").forGetter(ParticleAnimationPreset::maxSpawnDelay)
    ).apply(i, ParticleAnimationPreset::new));

    public ParticleAnimation build(ParticleAnimationPresetContext context) {
        return new ParticleAnimation(
                this.elements.stream().map(b -> b.build(context)).collect(Collectors.toUnmodifiableList()),
                this.finalizer.build(context),
                this.terminators,
                this.activationTriggers,
                this.spawner.build(context),
                this.minSpawnDelay,
                this.maxSpawnDelay
        );
    }
}
