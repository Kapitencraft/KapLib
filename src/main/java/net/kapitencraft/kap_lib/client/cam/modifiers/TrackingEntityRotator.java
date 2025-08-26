package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TrackingEntityRotator implements Modifier {
    private final Entity entity;
    private final EntityAnchorArgument.Anchor anchor;

    public TrackingEntityRotator(Entity entity, EntityAnchorArgument.Anchor anchor) {
        this.entity = entity;
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

        @Override
        public TrackingEntityRotator fromNetwork(FriendlyByteBuf buf) {
            Entity entity = NetworkHelper.entityFromNw(buf);
            EntityAnchorArgument.Anchor anchor = buf.readEnum(EntityAnchorArgument.Anchor.class);
            return new TrackingEntityRotator(entity, anchor);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, TrackingEntityRotator value) {
            buf.writeInt(value.entity.getId());
            buf.writeEnum(value.anchor);
        }
    }
}
