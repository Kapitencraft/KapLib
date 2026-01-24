package net.kapitencraft.kap_lib.core.client.util.pos_target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

/**
 * a position target returns the position of an entity. the anchor can be used to specify whether to return the feet position or the head position
 * @param target the target entity, as its entity id
 * @param anchor the anchor of the position. either feet or head
 */
public record EntityPositionTarget(int target, EntityAnchorArgument.Anchor anchor) implements PositionTarget {

    @Override
    public Vec3 get() {
        return anchor.apply(ClientHelper.getEntity(target));
    }

    @Override
    public Types getType() {
        return Types.ENTITY;
    }

    public static class Type implements PositionTarget.Type<EntityPositionTarget> {
        private static final MapCodec<EntityPositionTarget> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.INT.fieldOf("target").forGetter(t -> t.target),
                Codec.BOOL.fieldOf("target_head").xmap(b -> b ? EntityAnchorArgument.Anchor.EYES : EntityAnchorArgument.Anchor.FEET, a -> a == EntityAnchorArgument.Anchor.EYES).forGetter(EntityPositionTarget::anchor)
        ).apply(i, EntityPositionTarget::new));
        private static final StreamCodec<? super RegistryFriendlyByteBuf, EntityPositionTarget> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, t -> t.target,
                ExtraStreamCodecs.enumCodec(EntityAnchorArgument.Anchor.values()), t -> t.anchor,
                EntityPositionTarget::new
        );

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, EntityPositionTarget> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<EntityPositionTarget> codec() {
            return CODEC;
        }
    }

    @Override
    public String toString() {
        return "EntityPositionTarget[" + target + ']';
    }
}
