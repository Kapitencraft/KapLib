package net.kapitencraft.kap_lib.particle.animation.store;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTriggerInstance;

import java.util.List;

public class ParticleAnimationPreset {
    public static final Codec<ParticleAnimationPreset> CODEC = RecordCodecBuilder.create(i -> i.group(
            AnimationElement.CODEC.listOf().fieldOf("elements").forGetter(ParticleAnimation::allElements),
            ParticleFinalizer.CODEC.fieldOf("finalizer").forGetter(a -> a.finalizer),
            TerminationTrigger.CODEC.listOf().fieldOf("terminators").forGetter(ParticleAnimation::getTerminators),
            ActivationTrigger.CODEC.listOf().fieldOf("activator").forGetter(ParticleAnimation::getTriggers),
            Spawner.CODEC.fieldOf("spawner").forGetter(a -> a.spawner),
            Codec.INT.fieldOf("min_delay").forGetter(a -> a.minSpawnDelay),
            Codec.INT.fieldOf("max_delay").forGetter(a -> a.maxSpawnDelay)
    ).apply(i, ParticleAnimation::new));

    private final List<AnimationElement.Builder> elements;
    private final ParticleFinalizer.Builder finalizer;
    private final List<TerminationTriggerInstance>

    //TODO add a builder to the target providers in order to abstract the entities into a form where they can be dynamically created

    public static ParticleAnimationPreset fromJson(JsonObject object) {
        return null;
    }
}
