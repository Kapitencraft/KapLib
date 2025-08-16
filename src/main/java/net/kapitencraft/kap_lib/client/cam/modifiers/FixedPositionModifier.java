package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class FixedPositionModifier implements Modifier {
    private final Vec3 position;

    public FixedPositionModifier(Vec3 position) {
        this.position = position;
    }

    @Override
    public void modify(int tick, double percentage, CameraData data) {
        data.pos = position;
        data.detached = true;
    }

    @Override
    public Type getType() {
        return CameraModifiers.FIXED_POSITION.get();
    }

    public static class Type implements Modifier.Type<FixedPositionModifier> {
        public static final StreamCodec<? super FriendlyByteBuf, FixedPositionModifier> STREAM_CODEC = ExtraStreamCodecs.VEC_3.map(FixedPositionModifier::new, f -> f.position);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, FixedPositionModifier> codec() {
            return STREAM_CODEC;
        }
    }
}
