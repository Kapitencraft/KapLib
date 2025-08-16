package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.LibClient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ActivateShakePacket(float intensity, float strength, float frequency) implements CustomPacketPayload {
    public static final Type<ActivateShakePacket> TYPE = new Type<>(KapLibMod.res("activate_shake"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateShakePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ActivateShakePacket::intensity,
            ByteBufCodecs.FLOAT, ActivateShakePacket::strength,
            ByteBufCodecs.FLOAT, ActivateShakePacket::frequency,
            ActivateShakePacket::new
    );

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> LibClient.cameraControl.shake(this.intensity, this.strength, this.frequency));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
