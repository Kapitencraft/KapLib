package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;


public interface AnimationElement {
    @ApiStatus.Internal
    @OnlyIn(Dist.CLIENT)
    static AnimationElement fromNw(FriendlyByteBuf buf) {
        AnimationElement.Type<?> elementType = buf.readRegistryIdUnsafe(ExtraRegistries.ANIMATION_ELEMENT_TYPES);
        return elementType.fromNW(buf);
    }

    @ApiStatus.Internal
    static <T extends AnimationElement> void toNw(FriendlyByteBuf buf, T val) {
        AnimationElement.Type<T> type = (Type<T>) val.getType();
        buf.writeRegistryIdUnsafe(ExtraRegistries.ANIMATION_ELEMENT_TYPES, type);
        type.toNW(buf, val);
    }

    @NotNull Type<? extends AnimationElement> getType();


    /**
     * @param config the particle
     * @return the target time in ticks this element will take for the given particle
     */
    int createLength(ParticleConfig config);

    void tick(ParticleConfig object, int tick);

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

        T fromNW(FriendlyByteBuf buf);

        void toNW(FriendlyByteBuf buf, T value);
    }
}
