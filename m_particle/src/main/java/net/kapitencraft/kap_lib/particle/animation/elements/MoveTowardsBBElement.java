package net.kapitencraft.kap_lib.particle.animation.elements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.animation.target.EntityAccessor;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ElementTypes;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class MoveTowardsBBElement implements AnimationElement {
    private final UUID entity;
    private final int duration;

    public MoveTowardsBBElement(UUID entity, int duration) {
        this.entity = entity;
        this.duration = duration;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public @NotNull Type getType() {
        return ElementTypes.MOVE_TOWARDS_BB.get();
    }


    @Override
    public int createLength(ParticleConfig config) {
        return duration;
    }

    @Override
    public void initialize(ParticleConfig object) {
        object.setProperty("target", MathHelper.randomIn(MathHelper.RANDOM_SOURCE, ClientHelper.getEntity(entity).getBoundingBox()).subtract(ClientHelper.getEntity(entity).position()));
        object.setProperty("origin", object.pos());
    }

    @Override
    public void tick(ParticleConfig object, int tick, double percentage) {
        object.setPos(object.<Vec3>getProperty("origin")
                .lerp(object.<Vec3>getProperty("target")
                        .add(ClientHelper.getEntity(entity).position()), percentage)
        );
    }

    public static class Builder implements AnimationElement.Builder<MoveTowardsBBElement> {
        private static final MapCodec<MoveTowardsBBElement.Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                EntityAccessor.CODEC.fieldOf("entity").forGetter(e -> e.entity),
                Codec.INT.fieldOf("duration").forGetter(e -> e.duration)
        ).apply(i, MoveTowardsBBElement.Builder::fromCodec));

        private static Builder fromCodec(EntityAccessor entityAccessor, Integer integer) {
            return new Builder().target(entityAccessor).duration(integer);
        }


        private EntityAccessor entity;
        private int duration;

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder target(EntityAccessor entity) {
            this.entity = entity;
            return this;
        }

        public Builder target(Entity entity) {
            this.entity = EntityAccessor.direct(entity);
            return this;
        }

        @Override
        public MoveTowardsBBElement build(ParticleAnimationPresetContext context) {
            if (duration < 1) throw new IllegalStateException("MoveTowardsBB duration must be larger than 0");
            return new MoveTowardsBBElement(Objects.requireNonNull(entity.get(context), "MoveTowardsBB without entity found!"), duration);
        }

        @Override
        public AnimationElement.Type<MoveTowardsBBElement> type() {
            return ElementTypes.MOVE_TOWARDS_BB.get();
        }
    }

    public static class Type implements AnimationElement.Type<MoveTowardsBBElement> {

        private static final StreamCodec<? super RegistryFriendlyByteBuf, MoveTowardsBBElement> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, e -> e.entity,
                ByteBufCodecs.INT, e -> e.duration,
                MoveTowardsBBElement::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, MoveTowardsBBElement> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<MoveTowardsBBElement.Builder> codec() {
            return Builder.CODEC;
        }
    }
}
