package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * spawns particles inside the Bounding Box of an entity
 */
public class EntityBBSpawner extends VisibleSpawner {
    private final int targetId;
    private final boolean onlyOutline;
    private final float sizeXScale, sizeYScale;
    private final int perTick;

    protected EntityBBSpawner(ParticleOptions particle, int targetId, boolean onlyOutline, float sizeXScale, float sizeYScale, int perTick) {
        super(particle);
        this.targetId = targetId;
        this.onlyOutline = onlyOutline;
        this.sizeXScale = sizeXScale;
        this.sizeYScale = sizeYScale;
        this.perTick = perTick;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {
        Entity target = ClientHelper.getEntity(targetId);
        float bbRadius = target.getBbWidth() / 2;
        AABB box = target.getBoundingBox();
        for (int i = 0; i < perTick; i++) {
            Vec3 pos = MathHelper.randomIn(sink.random, box);
            if (onlyOutline) {
                float c = sink.random.nextFloat();
                if (c < 1f / 3) {
                    if (c < 1f / 6) pos = pos.with(Direction.Axis.X, box.minX);
                    else pos = pos.with(Direction.Axis.X, box.maxX);
                } else if (c < 2f / 3) {
                    if (c < .5) pos = pos.with(Direction.Axis.Y, box.minY);
                    else pos = pos.with(Direction.Axis.Y, box.maxY);
                } else {
                    if (c < 5f / 6) pos = pos.with(Direction.Axis.Z, box.minZ);
                    else pos = pos.with(Direction.Axis.Z, box.maxZ);
                }
            }
            sink.accept(this.particle, pos);
        }
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.ENTITY_BB.get();
    }

    public static class Type implements VisibleSpawner.Type<EntityBBSpawner> {

        @Override
        public void toNW(FriendlyByteBuf buf, EntityBBSpawner value) {
            NetworkHelper.writeParticleOptions(buf, value.particle);
            buf.writeInt(value.targetId);
            buf.writeBoolean(value.onlyOutline);
            buf.writeFloat(value.sizeXScale);
            buf.writeFloat(value.sizeYScale);
            buf.writeInt(value.perTick);
        }

        @Override
        public EntityBBSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return new EntityBBSpawner(NetworkHelper.readParticleOptions(buf),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readInt());
        }
    }

    public static class Builder extends VisibleSpawner.Builder<Builder> {
        private Entity target;
        private boolean onlyOutline;
        private float xScale = 1, yScale = 1;
        private int perTick;

        public Builder target(Entity target) {
            this.target = target;
            return this;
        }

        public Builder onlyOutline() {
            this.onlyOutline = true;
            return this;
        }

        public Builder scaleX(float xScale) {
            this.xScale *= xScale;
            return this;
        }

        public Builder scaleY(float yScale) {
            this.yScale *= yScale;
            return this;
        }

        @Override
        public VisibleSpawner build() {
            return new EntityBBSpawner(particle, target.getId(), onlyOutline, xScale, yScale, perTick);
        }

        public Builder perTick(int perTick) {
            this.perTick = perTick;
            return this;
        }
    }
}
