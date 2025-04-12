package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.FriendlyByteBuf;

public interface Modifier {

    void modify(int tick, double percentage, CameraData data);

    Modifier.Type<?> getType();

    static <T extends Modifier> void toNw(FriendlyByteBuf buf, T rotator) {
        buf.writeRegistryIdUnsafe(ExtraRegistries.CAMERA_MODIFIERS, rotator.getType());
        ((Modifier.Type<T>) rotator.getType()).toNetwork(buf, rotator);
    }

    static <T extends Modifier> T fromNw(FriendlyByteBuf buf) {
        Type<T> type = (Type<T>) buf.readRegistryIdUnsafe(ExtraRegistries.CAMERA_MODIFIERS);
        return type.fromNetwork(buf);
    }

    interface Type<T extends Modifier> {
        T fromNetwork(FriendlyByteBuf buf);

        void toNetwork(FriendlyByteBuf buf, T value);
    }
}
