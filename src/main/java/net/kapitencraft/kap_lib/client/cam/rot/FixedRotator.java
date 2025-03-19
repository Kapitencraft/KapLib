package net.kapitencraft.kap_lib.client.cam.rot;

import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.camera.Rotators;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class FixedRotator implements Rotator {
    private final Vec3 rot;

    public FixedRotator(Vec3 rot) {
        this.rot = rot;
    }

    @Override
    public Vec3 rotate(int ticks, Vec3 camPos) {
        return rot;
    }

    @Override
    public Type getType() {
        return Rotators.FIXED_ROTATION.get();
    }

    public static class Type implements Rotator.Type<FixedRotator> {

        @Override
        public FixedRotator fromNetwork(FriendlyByteBuf buf) {
            return new FixedRotator(NetworkHelper.readVec3(buf));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FixedRotator value) {
            NetworkHelper.writeVec3(buf, value.rot);
        }
    }
}
