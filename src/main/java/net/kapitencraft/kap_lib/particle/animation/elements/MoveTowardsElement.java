package net.kapitencraft.kap_lib.particle.animation.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ElementTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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

    public static class Builder implements AnimationElement.Builder<MoveTowardsElement> {
        private static final MapCodec<MoveTowardsElement.Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                PositionTarget.CODEC.fieldOf("target").forGetter(e -> e.targetLoc),
                Codec.INT.fieldOf("duration").forGetter(e -> e.duration)
        ).apply(i, Builder::fromCodec));

        private static Builder fromCodec(PositionTarget.Builder<?> builder, Integer integer) {
            return new Builder().target(builder).duration(integer);
        }

        private PositionTarget.Builder<?> targetLoc;
        private int duration;

        public Builder target(PositionTarget.Builder<?> pos) {
            targetLoc = pos;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @Override
        public MoveTowardsElement build(Map<String, Entity> context) {
            return new MoveTowardsElement(targetLoc.build(context), duration);
        }

        @Override
        public AnimationElement.Type<MoveTowardsElement> type() {
            return ElementTypes.MOVE_TOWARDS.get();
        }
    }

    public static class Type implements AnimationElement.Type<MoveTowardsElement> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, MoveTowardsElement> STREAM_CODEC = StreamCodec.composite(
                PositionTarget.STREAM_CODEC, e -> e.targetLoc,
                ByteBufCodecs.INT, e -> e.duration,
                MoveTowardsElement::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, MoveTowardsElement> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<MoveTowardsElement.Builder> codec() {
            return Builder.CODEC;
        }
    }
}
