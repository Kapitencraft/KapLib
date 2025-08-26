package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.cam.core.TrackingShot;
import net.kapitencraft.kap_lib.client.cam.core.TrackingShotData;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SendTrackingShotPacket implements SimplePacket {
    private final TrackingShotData shotData;

    public SendTrackingShotPacket(TrackingShotData shotData) {
        this.shotData = shotData;
    }

    public SendTrackingShotPacket(FriendlyByteBuf buf) {
        this(TrackingShotData.fromNw(buf));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        this.shotData.toNw(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> LibClient.cameraControl.activate(new TrackingShot(this.shotData)));
    }
}
