package net.kapitencraft.kap_lib.client.particle.animation.spawners;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.SpawnerTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * spawns particles inside the Bounding Box of an entity
 */
public class EntityBBSpawner extends Spawner {
    private final Entity target;
    private final boolean onlyOutline;
    private final float sizeXScale, sizeYScale;

    protected EntityBBSpawner(ParticleOptions particle, Entity target, boolean onlyOutline, float sizeXScale, float sizeYScale) {
        super(particle);
        this.target = target;
        this.onlyOutline = onlyOutline;
        this.sizeXScale = sizeXScale;
        this.sizeYScale = sizeYScale;
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {
        float bbRadius = target.getBbWidth() / 2;
        Vec3 min = target.position().add(-bbRadius, 0, -bbRadius);
        Vec3 max = target.position().add(bbRadius, target.getBbHeight(), bbRadius);
        Vec3 pos = MathHelper.randomBetween(sink.random, min, max);
        if (onlyOutline) {
            float c = sink.random.nextFloat();
            if (c < 1f / 3) {
                if (c < 1f / 6) pos = pos.with(Direction.Axis.X, target.position().x - bbRadius);
                else pos = pos.with(Direction.Axis.X, target.position().x + bbRadius);
            } else if (c < 2f / 3) {
                if (c < .5) pos = pos.with(Direction.Axis.Y, target.position().y);
                else pos = pos.with(Direction.Axis.Y, target.position().y + target.getBbHeight());
            } else {
                if (c < 5f / 6) pos = pos.with(Direction.Axis.Z, target.position().x - bbRadius);
                else pos = pos.with(Direction.Axis.Z, target.position().x + bbRadius);
            }
        }
        sink.accept(this.particle, pos);
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.ENTITY_BB.get();
    }

    public static class Type implements Spawner.Type<EntityBBSpawner> {

        @Override
        public void toNW(FriendlyByteBuf buf, EntityBBSpawner value) {
            NetworkHelper.writeParticleOptions(buf, value.particle);
            buf.writeInt(value.target.getId());
            buf.writeBoolean(value.onlyOutline);
            buf.writeFloat(value.sizeXScale);
            buf.writeFloat(value.sizeYScale);
        }

        @Override
        public EntityBBSpawner fromNw(FriendlyByteBuf buf, ClientLevel level) {
            return new EntityBBSpawner(NetworkHelper.readParticleOptions(buf),
                    Objects.requireNonNull(Minecraft.getInstance().level).getEntity(buf.readInt()),
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat()
            );
        }
    }

    public static class Builder extends Spawner.Builder<Builder> {
        private Entity target;
        private boolean onlyOutline;
        private float xScale = 1, yScale = 1;

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
        public Spawner build() {
            return new EntityBBSpawner(particle, target, onlyOutline, xScale, yScale);
        }
    }
}
