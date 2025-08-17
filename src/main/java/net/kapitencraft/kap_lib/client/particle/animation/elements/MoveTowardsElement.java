package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MoveTowardsElement implements AnimationElement {
    private final PositionTarget targetLoc;
    private final int duration;

    public MoveTowardsElement(PositionTarget targetLoc, int duration) {
        this.targetLoc = targetLoc;
        this.duration = duration;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void initialize(ParticleConfig object) {
        object.setProperty("origin", object.pos());
    }

    @Override
    public @NotNull AnimationElement.Type<? extends AnimationElement> getType() {
        return ElementTypes.MOVE_TOWARDS.get();
    }

    @Override
    public int createLength(ParticleConfig config) {
        return duration;
    }

    @Override
    public void tick(ParticleConfig object, int tick, double percentage) {
        object.setPos(object.<Vec3>getProperty("origin").lerp(targetLoc.get(), percentage));
    }

    public static class Builder implements AnimationElement.Builder {
        private PositionTarget targetLoc;
        private int duration;

        public Builder target(PositionTarget pos) {
            targetLoc = pos;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }


        @Override
        public AnimationElement build() {
            return new MoveTowardsElement(targetLoc, duration);
        }
    }

    public static class Type implements AnimationElement.Type<MoveTowardsElement> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, MoveTowardsElement> STREAM_CODEC = StreamCodec.composite(
                PositionTarget.STREAM_CODEC, e -> e.targetLoc,
                ByteBufCodecs.INT, e -> e.duration,
                MoveTowardsElement::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, MoveTowardsElement> codec() {
            return STREAM_CODEC;
        }
    }
}
