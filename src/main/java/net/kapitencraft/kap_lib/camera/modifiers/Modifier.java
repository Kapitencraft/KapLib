package net.kapitencraft.kap_lib.camera.modifiers;

import net.kapitencraft.kap_lib.camera.core.CameraData;
import net.kapitencraft.kap_lib.camera.registry.custom.CameraRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface Modifier {
    StreamCodec<RegistryFriendlyByteBuf, Modifier> CODEC = ByteBufCodecs.registry(CameraRegistries.Keys.CAMERA_MODIFIERS).dispatch(Modifier::getType, Type::codec);

    void modify(int tick, double percentage, CameraData data);

    Modifier.Type<?> getType();

    interface Type<T extends Modifier> {
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec();
    }
}
