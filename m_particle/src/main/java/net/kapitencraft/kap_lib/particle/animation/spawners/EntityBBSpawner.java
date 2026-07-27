package net.kapitencraft.kap_lib.particle.animation.spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.animation.target.EntityAccessor;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.SpawnerTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * spawns particles inside the Bounding Box of an entity
 */
public class EntityBBSpawner extends VisibleSpawner {
    private final UUID targetId;
    private final boolean onlyOutline;
    private final float sizeXScale, sizeYScale;
    private final int perTick;

    protected EntityBBSpawner(ParticleOptions particle, UUID targetId, boolean onlyOutline, float sizeXScale, float sizeYScale, int perTick) {
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
        private static final StreamCodec<? super RegistryFriendlyByteBuf, EntityBBSpawner> STREAM_CODEC = StreamCodec.composite(
                ParticleTypes.STREAM_CODEC, s -> s.particle,
                UUIDUtil.STREAM_CODEC, s -> s.targetId,
                ByteBufCodecs.BOOL, s -> s.onlyOutline,
                ByteBufCodecs.FLOAT, s -> s.sizeXScale,
                ByteBufCodecs.FLOAT, s -> s.sizeYScale,
                ByteBufCodecs.INT, s -> s.perTick,
                EntityBBSpawner::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, EntityBBSpawner> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<Builder> codec() {
            return Builder.CODEC;
        }
    }

    public static class Builder extends VisibleSpawner.Builder<Builder, EntityBBSpawner> {
        private static final MapCodec<Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ParticleTypes.CODEC.fieldOf("particle").forGetter(s -> s.particle),
                EntityAccessor.CODEC.fieldOf("target").forGetter(s -> s.target),
                Codec.BOOL.optionalFieldOf("outline", false).forGetter(s -> s.onlyOutline),
                Codec.FLOAT.optionalFieldOf("size_x_scale", 1f).forGetter(s -> s.xScale),
                Codec.FLOAT.optionalFieldOf("size_y_scale", 1f).forGetter(s -> s.yScale),
                Codec.INT.fieldOf("per_tick").forGetter(s -> s.perTick)
        ).apply(i, EntityBBSpawner.Builder::fromCodec));

        private static Builder fromCodec(ParticleOptions options, EntityAccessor entityAccessor, Boolean aBoolean, Float scaleX, Float scaleY, Integer countPerTick) {
            return new Builder().setParticle(options).target(entityAccessor).scaleX(scaleX).onlyOutline(aBoolean).scaleY(scaleY).perTick(countPerTick);
        }

        private EntityAccessor target;
        private boolean onlyOutline = false;
        private float xScale = 1, yScale = 1;
        private int perTick;

        public Builder target(Entity target) {
            this.target = EntityAccessor.direct(target);
            return this;
        }

        private Builder target(EntityAccessor accessor) {
            this.target = accessor;
            return this;
        }

        public Builder onlyOutline() {
            this.onlyOutline = true;
            return this;
        }

        private Builder onlyOutline(Boolean onlyOutline) {
            this.onlyOutline = onlyOutline;
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
        public EntityBBSpawner build(ParticleAnimationPresetContext context) {
            return new EntityBBSpawner(particle, target.get(context), onlyOutline, xScale, yScale, perTick);
        }

        @Override
        public Spawner.Type<EntityBBSpawner> type() {
            return SpawnerTypes.ENTITY_BB.get();
        }

        public Builder perTick(int perTick) {
            this.perTick = perTick;
            return this;
        }
    }
}
