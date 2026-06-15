package net.kapitencraft.kap_lib.camera.target.pos;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * a position target returns the position of an entity. the anchor can be used to specify whether to return the feet position or the head position
 * @param target the target entity, as its entity id
 * @param anchor the anchor of the position. either feet or head
 */
public record EntityPositionTarget(UUID target, EntityAnchorArgument.Anchor anchor) implements PositionTarget {

    @Override
    public Vec3 get() {
        return anchor.apply(ClientHelper.getEntity(target));
    }

    @Override
    public Types getType() {
        return Types.ENTITY;
    }

    public static class Type implements PositionTarget.Type<EntityPositionTarget> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, EntityPositionTarget> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, t -> t.target,
                ExtraStreamCodecs.enumCodec(EntityAnchorArgument.Anchor.values()), t -> t.anchor,
                EntityPositionTarget::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, EntityPositionTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<Builder> codec() {
            return Builder.CODEC;
        }
    }

    @Override
    public @NotNull String toString() {
        return "EntityPositionTarget[" + target + ']';
    }

    public static class Builder implements PositionTarget.Builder<EntityPositionTarget> {
        private static final MapCodec<Builder> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                UUIDUtil.CODEC.fieldOf("target").forGetter(b -> b.target),
                Codec.BOOL.fieldOf("target_head").xmap(b -> b ? EntityAnchorArgument.Anchor.EYES : EntityAnchorArgument.Anchor.FEET, a -> a == EntityAnchorArgument.Anchor.EYES).forGetter(b -> b.anchor)
        ).apply(i, Builder::fromCodec));

        private static Builder fromCodec(UUID accessor, EntityAnchorArgument.Anchor anchor) {
            return new Builder().setTarget(accessor).setAnchor(anchor);
        }


        private UUID target;
        private EntityAnchorArgument.Anchor anchor = EntityAnchorArgument.Anchor.FEET;

        public Builder setTarget(UUID target) {
            this.target = target;
            return this;
        }

        public Builder setAnchor(EntityAnchorArgument.Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder eyes() {
            this.anchor = EntityAnchorArgument.Anchor.EYES;
            return this;
        }

        @Override
        public EntityPositionTarget build() {
            return new EntityPositionTarget(target, this.anchor);
        }

        @Override
        public Types getType() {
            return Types.ENTITY;
        }
    }
}
