package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class FixedTargetPositionModifier implements Modifier {
    private final Vec3 pos;

    public FixedTargetPositionModifier(Vec3 pos) {
        this.pos = pos;
    }

    @Override
    public void modify(int ticks, double percentage, CameraData data) {
        data.rot = MathHelper.withRoll(MathHelper.createTargetRotationFromPos(data.pos, pos), 0);
    }

    @Override
    public Type getType() {
        return CameraModifiers.FIXED_TARGET_POSITION.get();
    }

    public static class Type implements Modifier.Type<FixedTargetPositionModifier> {

        @Override
        public FixedTargetPositionModifier fromNetwork(FriendlyByteBuf buf) {
            return new FixedTargetPositionModifier(ExtraStreamCodecs.readVec3(buf));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FixedTargetPositionModifier value) {
            ExtraStreamCodecs.writeVec3(buf, value.pos);
        }
    }
}
