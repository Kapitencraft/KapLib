package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GroupElement implements AnimationElement {
    private final List<AnimationElement> elements;

    public GroupElement(List<AnimationElement> elements) {
        this.elements = elements;
    }


    @Override
    public @NotNull Type getType() {
        return ElementTypes.GROUP.get();
    }

    @Override
    public int createLength(ParticleConfig config) {
        int max = 0;
        for (AnimationElement element : elements) {
            int check = element.createLength(config);
            if (check > max) max = check;
        }
        return max;
    }

    @Override
    public void tick(ParticleConfig object, int tick, double percentage) {
        for (AnimationElement element : elements) {
            element.tick(object, tick, percentage);
        }
    }

    public static class Type implements AnimationElement.Type<GroupElement> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, GroupElement> STREAM_CODEC = AnimationElement.CODEC.apply(ByteBufCodecs.list()).map(GroupElement::new, e -> e.elements);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, GroupElement> codec() {
            return STREAM_CODEC;
        }
    }

    public static class Builder implements AnimationElement.Builder {
        private final AnimationElement.Builder[] builders;

        public Builder(AnimationElement.Builder... builders) {
            this.builders = builders;
        }

        @Override
        public AnimationElement build() {
            return new GroupElement(Arrays.stream(builders).map(AnimationElement.Builder::build).toList());
        }
    }
}
