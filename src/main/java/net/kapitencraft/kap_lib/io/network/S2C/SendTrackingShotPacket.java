package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.cam.core.TrackingShot;
import net.kapitencraft.kap_lib.client.cam.core.TrackingShotData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendTrackingShotPacket(TrackingShotData shotData) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SendTrackingShotPacket> CODEC = TrackingShotData.CODEC.map(SendTrackingShotPacket::new, SendTrackingShotPacket::shotData);
    public static final Type<SendTrackingShotPacket> TYPE = new Type<>(KapLibMod.res("send_tracking_shot"));

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> LibClient.cameraControl.activate(new TrackingShot(this.shotData)));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
