package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ActivateShakePacket implements SimplePacket {
    private final float intensity, strength, frequency;

    public ActivateShakePacket(float intensity, float strength, float frequency) {
        this.intensity = intensity;
        this.strength = strength;
        this.frequency = frequency;
    }

    public ActivateShakePacket(FriendlyByteBuf buf) {
        this(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(this.intensity);
        buf.writeFloat(this.strength);
        buf.writeFloat(this.frequency);
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> LibClient.cameraControl.shake(this.intensity, this.strength, this.frequency));
        return true;
    }
}
