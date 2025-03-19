package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.cam.TrackingShot;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SendTrackingShotPacket implements SimplePacket {
    private final TrackingShot shot;

    public SendTrackingShotPacket(TrackingShot shot) {
        this.shot = shot;
    }

    public SendTrackingShotPacket(FriendlyByteBuf buf) {
        this(TrackingShot.fromNw(buf));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        this.shot.toNw(buf);
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            LibClient.cameraControl.activate(this.shot);
        });
        return true;
    }
}
