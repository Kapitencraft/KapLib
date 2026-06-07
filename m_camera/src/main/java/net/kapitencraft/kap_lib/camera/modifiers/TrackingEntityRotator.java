package net.kapitencraft.kap_lib.camera.modifiers;

import net.kapitencraft.kap_lib.camera.core.CameraData;
import net.kapitencraft.kap_lib.core.helpers.ClientHelper;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.camera.registry.CameraModifiers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class TrackingEntityRotator implements Modifier {
    private final Entity entity;
    private final EntityAnchorArgument.Anchor anchor;

    public TrackingEntityRotator(UUID entity, EntityAnchorArgument.Anchor anchor) {
        this.entity = ClientHelper.getEntity(entity);
        this.anchor = anchor;
    }

    @Override
    public void modify(int ticks, double percentage, CameraData data) {
        Vec3 pos = anchor.apply(entity);
        data.rot = MathHelper.withRoll(MathHelper.createTargetRotationFromPos(data.pos, pos), 0);
    }

    @Override
    public Type getType() {
        return CameraModifiers.TRACKING_ENTITY.get();
    }

    public static class Type implements Modifier.Type<TrackingEntityRotator> {
        private static final StreamCodec<? super FriendlyByteBuf, EntityAnchorArgument.Anchor> ANCHOR_STREAM_CODEC = ByteBufCodecs.idMapper(value -> EntityAnchorArgument.Anchor.values()[value], EntityAnchorArgument.Anchor::ordinal);
        private static final StreamCodec<? super FriendlyByteBuf, TrackingEntityRotator> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, r -> r.entity.getUUID(),
                ANCHOR_STREAM_CODEC, r -> r.anchor,
                TrackingEntityRotator::new
        );


        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, TrackingEntityRotator> codec() {
            return STREAM_CODEC;
        }
    }
}
