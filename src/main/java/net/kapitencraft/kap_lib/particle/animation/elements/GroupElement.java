package net.kapitencraft.kap_lib.particle.animation.elements;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ElementTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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
        private static final MapCodec<GroupElement> CODEC = AnimationElement.CODEC.listOf().xmap(GroupElement::new, e -> e.elements).fieldOf("elements");
        private static final StreamCodec<? super RegistryFriendlyByteBuf, GroupElement> STREAM_CODEC = AnimationElement.STREAM_CODEC.apply(ByteBufCodecs.list()).map(GroupElement::new, e -> e.elements);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, GroupElement> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<GroupElement> codec() {
            return CODEC;
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
