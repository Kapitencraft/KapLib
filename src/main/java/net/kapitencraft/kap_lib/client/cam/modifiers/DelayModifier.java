package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.minecraft.network.FriendlyByteBuf;

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

        @Override
        public DelayModifier fromNetwork(FriendlyByteBuf buf) {
            return INSTANCE;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DelayModifier value) {

        }
    }
}
