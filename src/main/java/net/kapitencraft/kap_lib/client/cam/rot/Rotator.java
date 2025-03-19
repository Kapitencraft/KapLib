package net.kapitencraft.kap_lib.client.cam.rot;

import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public interface Rotator {

    /**
     * @param ticks the tick count
     * @param camPos the position of the camera
     * @return the rotation of the camera with values yaw, pitch and roll
     */
    Vec3 rotate(int ticks, Vec3 camPos);

    Type<?> getType();

    static <T extends Rotator> void toNw(FriendlyByteBuf buf, T rotator) {
        buf.writeRegistryIdUnsafe(ExtraRegistries.CAMERA_ROTATORS, rotator.getType());
        ((Type<T>) rotator.getType()).toNetwork(buf, rotator);
    }

    static <T extends Rotator> T fromNw(FriendlyByteBuf buf) {
        Type<T> type = (Type<T>) buf.readRegistryIdUnsafe(ExtraRegistries.CAMERA_ROTATORS);
        return type.fromNetwork(buf);
    }

    interface Type<T extends Rotator> {
        T fromNetwork(FriendlyByteBuf buf);

        void toNetwork(FriendlyByteBuf buf, T value);
    }
}
