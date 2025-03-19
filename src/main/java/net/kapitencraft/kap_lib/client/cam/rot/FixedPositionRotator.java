package net.kapitencraft.kap_lib.client.cam.rot;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.camera.Rotators;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class FixedPositionRotator implements Rotator {
    private final Vec3 pos;

    public FixedPositionRotator(Vec3 pos) {
        this.pos = pos;
    }

    @Override
    public Vec3 rotate(int ticks, Vec3 camPos) {
        return MathHelper.withRoll(MathHelper.createTargetRotationFromPos(camPos, pos), 0);
    }

    @Override
    public Type getType() {
        return Rotators.FIXED_POSITION.get();
    }

    public static class Type implements Rotator.Type<FixedPositionRotator> {

        @Override
        public FixedPositionRotator fromNetwork(FriendlyByteBuf buf) {
            return new FixedPositionRotator(NetworkHelper.readVec3(buf));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FixedPositionRotator value) {
            NetworkHelper.writeVec3(buf, value.pos);
        }
    }
}
