package net.kapitencraft.kap_lib.camera.network.S2C;

import net.kapitencraft.kap_lib.camera.core.CameraController;
import net.kapitencraft.kap_lib.camera.core.TrackingShot;
import net.kapitencraft.kap_lib.camera.core.TrackingShotData;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SendTrackingShotPacket(TrackingShotData shotData) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SendTrackingShotPacket> CODEC = TrackingShotData.CODEC.map(SendTrackingShotPacket::new, SendTrackingShotPacket::shotData);
    public static final Type<SendTrackingShotPacket> TYPE = new Type<>(LibConstants.res("send_tracking_shot"));

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> CameraController.INSTANCE.activate(new TrackingShot(this.shotData)));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}