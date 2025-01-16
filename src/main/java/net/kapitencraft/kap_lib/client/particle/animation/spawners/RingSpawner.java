package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.client.particle.animation.util.rot_target.RotationTarget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
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

    private RingSpawner(PositionTarget target, ParticleOptions particle, RotationTarget rotation, Direction.Axis axis, float rotPerTick, float maxHeight, float heightChangePerTick, float radius, int spawnerCount) {
        super(particle);
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
            double sin = Math.sin(Math.toRadians(curRot + angleBetweenSpawner * i)) * radius;
            double cos = Math.cos(Math.toRadians(curRot + angleBetweenSpawner * i)) * radius;
            double x = axis == Direction.Axis.X ? 0 : sin,
                    y = switch (axis) {
                case X -> sin;
                case Y -> 0;
                case Z -> cos;
                },
                    z = axis == Direction.Axis.Z ? 0 : cos;
            Vec3 targetOffset = new Vec3(x, y, z);
            switch (axis) {
                case X -> {
                    targetOffset = MathHelper.rotateHorizontalYAxis(targetOffset, Vec3.ZERO, rot.x);
                    targetOffset = MathHelper.rotateZAxis(targetOffset, Vec3.ZERO, rot.y);
                }
                case Y -> {
                    targetOffset = MathHelper.rotateXAxis(targetOffset, Vec3.ZERO, rot.x);
                    targetOffset = MathHelper.rotateZAxis(targetOffset, Vec3.ZERO, rot.y);
                }
                case Z -> {
                    targetOffset = MathHelper.rotateXAxis(targetOffset, Vec3.ZERO, rot.x);
                    targetOffset = MathHelper.rotateHorizontalYAxis(targetOffset, Vec3.ZERO, rot.y);
                }
            }
            Vec3 targetPos = targetOffset.add(Vec3.ZERO.with(axis, curHeightChange)).add(target.get());
            sink.accept(particle, targetPos);
            curRot += rotPerTick;
            if (heightChangePerTick > 0) applyHeightChange();
        }
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

        @Override
        public RingSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            PositionTarget target = PositionTarget.fromNw(buf);
            ParticleOptions options = NetworkHelper.readParticleOptions(buf);
            Direction.Axis axis = Direction.Axis.values()[buf.readInt()];
            return new RingSpawner(target, options, RotationTarget.fromNw(buf), axis, buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt());
        }

        @Override
        public void toNW(FriendlyByteBuf buf, RingSpawner value) {
            value.target.toNw(buf);
            NetworkHelper.writeParticleOptions(buf, value.particle);
            buf.writeInt(value.axis.ordinal());
            value.rotation.toNw(buf);
            buf.writeFloat(value.rotPerTick);
            buf.writeFloat(value.maxHeight);
            buf.writeFloat(value.heightChangePerTick);
            buf.writeFloat(value.radius);
            buf.writeInt(value.spawnerCount);
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
        return builder().setTarget(PositionTarget.entity(entity)).axis(Direction.Axis.Y).maxHeight(entity.getBbHeight() * yScale).radius(bbRadius * xScale);
    }

    /**
     * a new Builder
     */
    public static class Builder extends VisibleSpawner.Builder<Builder> {
        private PositionTarget target;
        private float rotPerTick, maxHeight, heightChangePerTick, radius;
        private int spawnCount = 1;
        private Direction.Axis axis;
        private RotationTarget rotationTarget = RotationTarget.absolute(Vec2.ZERO);

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
        public Builder setTarget(PositionTarget target) {
            this.target = target;
            return this;
        }

        public Builder rotation(RotationTarget target) {
            this.rotationTarget = target;
            return this;
        }

        @Override
        public VisibleSpawner build() {
            return new RingSpawner(target, particle, rotationTarget, axis, rotPerTick, maxHeight, heightChangePerTick, radius, spawnCount);
        }
    }

    @Override
    public String toString() {
        return "RingSpawner{" +
                "particle=" + particle +
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
