package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.lwjgl.openal.EXTStereoAngles;

public class GlideTowardsModifier implements Modifier {
    private final PositionTarget origin, target;

    public GlideTowardsModifier(PositionTarget origin, PositionTarget target) {
        this.origin = origin;
        this.target = target;
    }


    @Override
    public void modify(int tick, double percentage, CameraData data) {
        data.pos = origin.get().lerp(target.get(), percentage);
        data.detached = true;
    }

    @Override
    public Type getType() {
        return CameraModifiers.GLIDE_TOWARDS.get();
    }

    public static class Type implements Modifier.Type<GlideTowardsModifier> {
        private static final StreamCodec<FriendlyByteBuf, GlideTowardsModifier> STREAM_CODEC = StreamCodec.composite(
                PositionTarget.STREAM_CODEC, m -> m.origin,
                PositionTarget.STREAM_CODEC, m -> m.target,
                GlideTowardsModifier::new
        );


        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, GlideTowardsModifier> codec() {
            return STREAM_CODEC;
        }
    }
}
