package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GroupElement implements AnimationElement {
    private final AnimationElement[] elements;

    public GroupElement(AnimationElement[] elements) {
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

        @Override
        public GroupElement fromNW(FriendlyByteBuf buf) {
            AnimationElement[] elements = ExtraStreamCodecs.readArray(buf, AnimationElement[]::new, AnimationElement::fromNw);
            return new GroupElement(elements);
        }

        @Override
        public void toNW(FriendlyByteBuf buf, GroupElement value) {
            ExtraStreamCodecs.writeArray(buf, value.elements, AnimationElement::toNw);
        }
    }

    public static class Builder implements AnimationElement.Builder {
        private final AnimationElement.Builder[] builders;

        public Builder(AnimationElement.Builder... builders) {
            this.builders = builders;
        }

        @Override
        public AnimationElement build() {
            return new GroupElement(Arrays.stream(builders).map(AnimationElement.Builder::build).toArray(AnimationElement[]::new));
        }
    }
}
