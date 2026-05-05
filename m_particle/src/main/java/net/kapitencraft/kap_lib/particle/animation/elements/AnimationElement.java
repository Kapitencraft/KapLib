package net.kapitencraft.kap_lib.particle.animation.elements;

import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;


public interface AnimationElement {
    /**
     * base StreamCodec for animation elements
     */
    StreamCodec<RegistryFriendlyByteBuf, AnimationElement> CODEC = ByteBufCodecs.registry(ParticleAnimationRegistries.Keys.MODIFIER_TYPES).dispatch(AnimationElement::getType, Type::codec);

    /**
     * provides the type of this element. return a value registered to the registry
     * @return the registered type of this element
     * @see Type
     */
    @NotNull Type<? extends AnimationElement> getType();

    /**
     * @param config the particle
     * @return the target time in ticks this element will take for the given particle
     */
    int createLength(ParticleConfig config);

    /**
     * called each tick for every particle config in the animation
     * @param object the particle to be animated
     * @param tick the amount of ticks passed for this element
     * @param percentage the percentage of time passed for this element
     */
    void tick(ParticleConfig object, int tick, double percentage);

    /**
     * called when this element start taking over the animation of the given config
     *
     * @param object the config being initialized
     */
    default void initialize(ParticleConfig object) {

    }

    /**
     * called when this element has completed animating the given config
     *
     * @param config the config being finalized
     */
    default void finalize(ParticleConfig config) {
    }

    /**
     * builder for Animation elements. override in your own animation elements to use them in animations
     */
    interface Builder {

        /**
         * builds this element
         * @return the build element
         */
        AnimationElement build();
    }

    /**
     * the type of the element. must be registered to the <br> {@link ParticleAnimationRegistries#ANIMATION_ELEMENT_TYPES} registry in order to work
     * @param <T> class type of the element
     */
    interface Type<T extends AnimationElement> {

        /**
         * StreamCodec supplier to transfer element data to the client for rendering
         * @return the StreamCodec used to transfer its data to the client
         */
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec();
    }
}
