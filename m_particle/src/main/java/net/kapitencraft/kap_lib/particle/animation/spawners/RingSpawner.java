package net.kapitencraft.kap_lib.particle.animation.spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.animation.target.pos.PositionTarget;
import net.kapitencraft.kap_lib.particle.animation.target.rot.RotationTarget;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.SpawnerTypes;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RingSpawner extends VisibleSpawner {
    private final PositionTarget target;
    private final RotationTarget rotation;
    private float curRot, curHeightChange;
    private boolean rising;
    private final float rotPerTick, maxHeight, heightChangePerTick, radius;
    private final float angleBetweenSpawner;
    private final int spawnerCount;
    private final Direction.Axis axis;

    private RingSpawner(PositionTarget target, String texture, RotationTarget rotation,
                        Direction.Axis axis, float rotPerTick, float maxHeight, float heightChangePerTick, float radius, int spawnerCount) {
        super(texture);
        if (radius <= 0) throw new IllegalStateException("Ring-Spawner radius must be larger than 0!");
        this.rotation = Objects.requireNonNull(rotation, "Ring-Spawner no rotation specified!");
        this.target = Objects.requireNonNull(target, "Ring-Spawner no target specified!");
        this.axis = Objects.requireNonNull(axis, "Ring-Spawner no axis specified");
        this.rotPerTick = rotPerTick;
        this.maxHeight = maxHeight;
        this.heightChangePerTick = heightChangePerTick;
        this.radius = radius;
        this.spawnerCount = spawnerCount;
        this.angleBetweenSpawner = 360f / spawnerCount;
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.RING.get();
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {
        Vec2 rot = rotation.get();
        for (int i = 0; i < spawnerCount; i++) {
            Vec3 targetOffset = getTargetOffset(i);
            targetOffset = MathHelper.rotateXAxis(targetOffset, Vec3.ZERO, -rot.x * Mth.DEG_TO_RAD);
            targetOffset = MathHelper.rotateHorizontalYAxis(targetOffset, Vec3.ZERO, -rot.y * Mth.DEG_TO_RAD);
            Vec3 targetPos = targetOffset.add(Vec3.ZERO.with(axis, curHeightChange)).add(target.get());
            sink.accept(targetPos, texture);
            curRot += rotPerTick;
            if (heightChangePerTick > 0) applyHeightChange();
        }
    }

    private @NotNull Vec3 getTargetOffset(int i) {
        double sin = Math.sin(Math.toRadians(curRot + angleBetweenSpawner * i)) * radius;
        double cos = Math.cos(Math.toRadians(curRot + angleBetweenSpawner * i)) * radius;
        double x = axis == Direction.Axis.X ? 0 : sin,
                y = switch (axis) {
                    case X -> sin;
                    case Y -> 0;
                    case Z -> cos;
                }, z = axis == Direction.Axis.Z ? 0 : cos;
        return new Vec3(x, y, z);
    }

    @ApiStatus.Internal
    private void applyHeightChange() {
        if (rising) {
            float distanceToMax = maxHeight - curHeightChange;
            if (distanceToMax < heightChangePerTick) {
                curHeightChange = maxHeight - (heightChangePerTick - distanceToMax);
                rising = false;
            } else {
                curHeightChange += heightChangePerTick;
            }
        } else {
            if (curHeightChange < heightChangePerTick) {
                curHeightChange = heightChangePerTick - curHeightChange;
                rising = true;
            } else {
                curHeightChange -= heightChangePerTick;
            }
        }
    }

    public static class Type implements VisibleSpawner.Type<RingSpawner> {
        private static final StreamCodec<RegistryFriendlyByteBuf, RingSpawner> STREAM_CODEC = ExtraStreamCodecs.composite(
                PositionTarget.STREAM_CODEC, s -> s.target,
                ByteBufCodecs.STRING_UTF8, s -> s.texture,
                RotationTarget.STREAM_CODEC, s -> s.rotation,
                ExtraStreamCodecs.enumCodec(Direction.Axis.values()), s -> s.axis,
                ByteBufCodecs.FLOAT, s -> s.rotPerTick,
                ByteBufCodecs.FLOAT, s -> s.maxHeight,
                ByteBufCodecs.FLOAT, s -> s.heightChangePerTick,
                ByteBufCodecs.FLOAT, s -> s.radius,
                ByteBufCodecs.INT, s -> s.spawnerCount,
                RingSpawner::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RingSpawner> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<Builder> codec() {
            return Builder.CODEC;
        }
    }

    /**
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @param spawnerCount the amount of particles of the circle
     * @return a new builder without height, no rotation and set amount of particles
     */
    public static Builder fullCircle(int spawnerCount) {
        return noHeight().spawnCount(spawnerCount).rotPerTick(0);
    }

    /**
     * @return a new builder without height
     */
    public static Builder noHeight() {
        return new Builder().maxHeight(0).heightPerTick(0);
    }

    /**
     * @param entity the target entity
     * @param xScale a scale to the radius
     * @param yScale a scale to the height
     */
    public static Builder entityWithBBSize(Entity entity, float xScale, float yScale) {
        float bbRadius = entity.getBbWidth() / 2;
        return builder().setTarget(PositionTarget.entity(entity)).axis(Direction.Axis.Y)
                .maxHeight(entity.getBbHeight() * yScale).radius(bbRadius * xScale);
    }

    public static Builder entityWithBBSize(String entityKey, float width, float height) {
        return builder().setTarget(PositionTarget.entity(entityKey)).axis(Direction.Axis.Y)
                .maxHeight(height).radius(width / 2);
    }

    /**
     * a new Builder
     */
    public static class Builder extends VisibleSpawner.Builder<Builder, RingSpawner> {
        private static final MapCodec<RingSpawner.Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                PositionTarget.CODEC.fieldOf("target").forGetter(s -> s.target),
                Codec.STRING.fieldOf("particle").forGetter(s -> s.texture),
                RotationTarget.CODEC.fieldOf("rotation").forGetter(s -> s.rotationTarget),
                Direction.Axis.CODEC.fieldOf("axis").forGetter(s -> s.axis),
                Codec.FLOAT.fieldOf("rot_speed").forGetter(s -> s.rotPerTick),
                Codec.FLOAT.fieldOf("max_height").forGetter(s -> s.maxHeight),
                Codec.FLOAT.fieldOf("height_change").forGetter(s -> s.heightChangePerTick),
                Codec.FLOAT.fieldOf("radius").forGetter(s -> s.radius),
                Codec.INT.fieldOf("count").forGetter(s -> s.spawnCount)
        ).apply(i, RingSpawner.Builder::fromCodec));

        private static Builder fromCodec(PositionTarget.Builder<?> builder, String texture, RotationTarget.Builder<?> rotationTarget, Direction.Axis axis, Float rotPerTick, Float maxHeight, Float heightPerTick, Float radius, Integer spawnCount) {
            return new Builder()
                    .setTarget(builder).setTexture(texture).rotation(rotationTarget).axis(axis)
                    .rotPerTick(rotPerTick).maxHeight(maxHeight).heightPerTick(heightPerTick)
                    .radius(radius).spawnCount(spawnCount);
        }


        private PositionTarget.Builder<?> target;
        private float rotPerTick, maxHeight, heightChangePerTick, radius;
        private int spawnCount = 1;
        private Direction.Axis axis;
        private RotationTarget.Builder<?> rotationTarget = RotationTarget.absolute(Vec2.ZERO);

        /**
         * @param rotPerTick Developer Note: do I need to explain this?
         */
        public Builder rotPerTick(float rotPerTick) {
            this.rotPerTick = rotPerTick;
            return this;
        }

        /**
         * @param maxHeight the maximum height the spawner will go up to
         */
        public Builder maxHeight(float maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        /**
         * @param heightChangePerTick how quick the height offset of the spawner should update
         */
        public Builder heightPerTick(float heightChangePerTick) {
            this.heightChangePerTick = heightChangePerTick;
            return this;
        }

        /**
         * @param radius the radius of the spawning ring
         */
        public Builder radius(float radius) {
            this.radius = radius;
            return this;
        }

        /**
         * @param count the amount of particle spawners
         */
        public Builder spawnCount(int count) {
            this.spawnCount = count;
            return this;
        }

        /**
         * @param axis the axis to rotate around
         */
        public Builder axis(Direction.Axis axis) {
            this.axis = axis;
            return this;
        }

        /**
         * @param target the target, center point of the ring
         */
        public Builder setTarget(PositionTarget.Builder<?> target) {
            this.target = target;
            return this;
        }

        public Builder rotation(RotationTarget.Builder<?> target) {
            this.rotationTarget = target;
            return this;
        }

        @Override
        public RingSpawner build(ParticleAnimationPresetContext context) {
            return new RingSpawner(target.build(context), this.texture, rotationTarget.build(context), axis, rotPerTick, maxHeight, heightChangePerTick, radius, spawnCount);
        }

        @Override
        public Type type() {
            return SpawnerTypes.RING.get();
        }
    }

    @Override
    public String toString() {
        return "RingSpawner{" +
                "texture=" + texture +
                ", axis=" + axis +
                ", spawnerCount=" + spawnerCount +
                ", angleBetweenSpawner=" + angleBetweenSpawner +
                ", radius=" + radius +
                ", heightChangePerTick=" + heightChangePerTick +
                ", maxHeight=" + maxHeight +
                ", rotPerTick=" + rotPerTick +
                ", rising=" + rising +
                ", curHeightChange=" + curHeightChange +
                ", curRot=" + curRot +
                ", target=" + target +
                '}';
    }
}
