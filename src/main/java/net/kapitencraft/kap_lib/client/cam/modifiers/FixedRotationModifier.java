package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
        private static final StreamCodec<? super FriendlyByteBuf, FixedRotationModifier> STREAM_CODEC = ExtraStreamCodecs.VEC_3.map(FixedRotationModifier::new, m -> m.rot);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, FixedRotationModifier> codec() {
            return STREAM_CODEC;
        }
    }
}
