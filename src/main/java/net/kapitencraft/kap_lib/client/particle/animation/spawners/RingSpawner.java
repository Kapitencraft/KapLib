package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RingSpawner extends Spawner {
    private final PositionTarget target;
    private float curRot, curHeightChange;
    private boolean rising;
    private final float rotPerTick, maxHeight, heightChangePerTick, distance;
    private final float angleBetweenSpawner;
    private final int spawnerCount;
    private final Direction.Axis axis;

    private RingSpawner(PositionTarget target, ParticleOptions particle, Direction.Axis axis, float rotPerTick, float maxHeight, float heightChangePerTick, float distance, int spawnerCount) {
        super(particle);
        this.target = Objects.requireNonNull(target, "no target specified!");
        this.axis = Objects.requireNonNull(axis, "no axis specified");
        this.rotPerTick = rotPerTick;
        this.maxHeight = maxHeight;
        this.heightChangePerTick = heightChangePerTick;
        this.distance = distance;
        this.spawnerCount = spawnerCount;
        this.angleBetweenSpawner = 360f / spawnerCount;
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.RING_SPAWN.get();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void spawn(ParticleSpawnSink sink) {
        for (int i = 0; i < spawnerCount; i++) {
            double sin = Math.sin(Math.toRadians(curRot + angleBetweenSpawner * i)) * distance;
            double cos = Math.cos(Math.toRadians(curRot + angleBetweenSpawner * i)) * distance;
            Vec3 targetOffset = switch (axis) {
                case X -> new Vec3(curHeightChange, sin, cos);
                case Y -> new Vec3(sin, curHeightChange, cos);
                case Z -> new Vec3(sin, cos, curHeightChange);
            };
            Vec3 targetPos = targetOffset.add(target.pos());
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

    public static class Type implements Spawner.Type<RingSpawner> {

        @Override
        public RingSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            PositionTarget target = PositionTarget.fromNw(buf);
            ParticleOptions options = NetworkHelper.readParticleOptions(buf);
            Direction.Axis axis = Direction.Axis.values()[buf.readInt()];
            return new RingSpawner(target, options, axis, buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt());
        }

        @Override
        public void toNW(FriendlyByteBuf buf, RingSpawner value) {
            value.target.toNw(buf);
            NetworkHelper.writeParticleOptions(buf, value.particle);
            buf.writeInt(value.axis.ordinal());
            buf.writeFloat(value.rotPerTick);
            buf.writeFloat(value.maxHeight);
            buf.writeFloat(value.heightChangePerTick);
            buf.writeFloat(value.distance);
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
    public static class Builder extends Spawner.Builder<Builder> {
        private PositionTarget target;
        private float rotPerTick, maxHeight, heightChangePerTick, radius;
        private int spawnCount = 1;
        private Direction.Axis axis;

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

        @Override
        public Spawner build() {
            return new RingSpawner(target, particle, axis, rotPerTick, maxHeight, heightChangePerTick, radius, spawnCount);
        }
    }
}
