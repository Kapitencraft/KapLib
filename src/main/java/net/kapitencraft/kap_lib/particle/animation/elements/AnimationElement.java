package net.kapitencraft.kap_lib.particle.animation.elements;

import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;


public interface AnimationElement {
    StreamCodec<RegistryFriendlyByteBuf, AnimationElement> CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.MODIFIER_TYPES).dispatch(AnimationElement::getType, Type::codec);

    @NotNull Type<? extends AnimationElement> getType();

    /**
     * @param config the particle
     * @return the target time in ticks this element will take for the given particle
     */
    int createLength(ParticleConfig config);

    void tick(ParticleConfig object, int tick, double percentage);

    /**
     * called when this element start taking over the animation of the given config
     * @param object the config being initialized
     */
    default void initialize(ParticleConfig object) {

    }

    /**
     * called when this element has completed animating the given config
     * @param config the config being finalized
     */
    default void finalize(ParticleConfig config) {

    }

    /**
     * builder for Animation elements. override in your own animation elements to use them in animations
     */
    interface Builder {

        AnimationElement build();
    }

    interface Type<T extends AnimationElement> {
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec();
    }
}
