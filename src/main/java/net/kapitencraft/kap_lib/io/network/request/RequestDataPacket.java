package net.kapitencraft.kap_lib.io.network.request;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestDataPacket<T, K> implements SimplePacket {
    private final IRequestable<T, K> provider;
    private final short id;
    private final T data;

    public RequestDataPacket(FriendlyByteBuf buf) {
        this.id = buf.readShort();
        this.provider = RequestPacket.getRequestable(buf.readUtf());
        this.data = this.provider.getFromNetwork(buf);
    }

    public short getId() {
        return id;
    }

    public RequestDataPacket(short requestId, IRequestable<T, K> provider, T data) {
        this.id = requestId;
        this.provider = provider;
        this.data = data;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeShort(this.id);
        buf.writeUtf(RequestPacket.saveRequestable(this.provider));
        this.provider.writeToNetwork(this.data, buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context context = sup.get();
        context.enqueueWork(() -> LibClient.requests.accordPackageReceive(this));
    }

    public T getData() {
        return data;
    }
}
