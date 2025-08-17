package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class DelayModifier implements Modifier {
    public static final DelayModifier INSTANCE = new DelayModifier();

    @Override
    public void modify(int tick, double percentage, CameraData data) {
    }

    @Override
    public Type getType() {
        return null;
    }

    public static class Type implements Modifier.Type<DelayModifier> {
        private static final StreamCodec<? super FriendlyByteBuf, DelayModifier> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, DelayModifier> codec() {
            return STREAM_CODEC;
        }
    }
}
