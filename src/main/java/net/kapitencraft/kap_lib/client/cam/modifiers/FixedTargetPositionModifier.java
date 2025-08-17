package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
        private static final StreamCodec<? super FriendlyByteBuf, FixedTargetPositionModifier> STREAM_CODEC = ExtraStreamCodecs.VEC_3.map(FixedTargetPositionModifier::new, m -> m.pos);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, FixedTargetPositionModifier> codec() {
            return STREAM_CODEC;
        }
    }
}
