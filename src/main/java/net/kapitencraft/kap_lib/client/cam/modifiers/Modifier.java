package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.fml.common.Mod;

public interface Modifier {
    StreamCodec<RegistryFriendlyByteBuf, Modifier> CODEC = ByteBufCodecs.registry(ExtraRegistries.Keys.CAMERA_MODIFIERS).dispatch(Modifier::getType, Type::codec);

    void modify(int tick, double percentage, CameraData data);

    Modifier.Type<?> getType();

    interface Type<T extends Modifier> {
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec();
    }
}
