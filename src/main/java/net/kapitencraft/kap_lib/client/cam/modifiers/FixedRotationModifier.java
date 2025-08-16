package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class FixedRotationModifier implements Modifier {
    private final Vec3 rot;

    public FixedRotationModifier(Vec3 rot) {
        this.rot = rot;
    }

    @Override
    public void modify(int ticks, double percentage, CameraData data) {
        data.rot = rot;
    }

    @Override
    public Type getType() {
        return CameraModifiers.FIXED_ROTATION.get();
    }

    public static class Type implements Modifier.Type<FixedRotationModifier> {

        @Override
        public FixedRotationModifier fromNetwork(FriendlyByteBuf buf) {
            return new FixedRotationModifier(ExtraStreamCodecs.readVec3(buf));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FixedRotationModifier value) {
            ExtraStreamCodecs.writeVec3(buf, value.rot);
        }
    }
}
