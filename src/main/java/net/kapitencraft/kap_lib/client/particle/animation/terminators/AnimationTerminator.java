package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public interface AnimationTerminator {

    static AnimationTerminator fromNw(FriendlyByteBuf buf) {
        AnimationTerminator.Type<?> type = buf.readRegistryIdUnsafe(ExtraRegistries.ANIMATION_TERMINATOR_TYPES);
        return type.fromNw(buf);
    }

    static <T extends AnimationTerminator> void toNw(FriendlyByteBuf buf, T val) {
        AnimationTerminator.Type<T> type = (Type<T>) val.getType();
        buf.writeRegistryIdUnsafe(ExtraRegistries.ANIMATION_TERMINATOR_TYPES, type);
        type.toNw(buf, val);
    }

    @NotNull Type<? extends AnimationTerminator> getType();

    boolean shouldTerminate(ParticleAnimator animation);

    interface Type<T extends AnimationTerminator> {

        void toNw(FriendlyByteBuf buf, T val);

        T fromNw(FriendlyByteBuf buf);
    }

    interface Builder {
        AnimationTerminator build();
    }
}
