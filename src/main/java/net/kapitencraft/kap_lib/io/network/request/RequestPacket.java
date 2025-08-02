package net.kapitencraft.kap_lib.io.network.request;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class RequestPacket<T, K> implements SimplePacket {
    private final short requestId;
    private final IRequestable<T, K> provider;
    private final K value;

    public RequestPacket(short requestId, IRequestable<T, K> provider, K value) {
        this.requestId = requestId;
        this.provider = provider;
        this.value = value;
    }

    public RequestPacket(FriendlyByteBuf buf) {
        this.requestId = buf.readShort();
        this.provider = getRequestable(buf.readUtf());
        this.value = provider.readRequest(buf);
    }

    public static <T, K> IRequestable<T, K> getRequestable(String id) {
        ResourceLocation location = new ResourceLocation(id);
        return (IRequestable<T, K>) Objects.requireNonNull(ExtraRegistries.REQUESTABLES.getValue(location), "unable to read requestable for key '" + id + "'");
    }

    public static <T, K> String saveRequestable(IRequestable<T, K> requestable) {
        ResourceLocation location = ExtraRegistries.REQUESTABLES.getKey(requestable);
        if (location == null) throw new IllegalStateException("can not send request without valid requestable");
        return location.toString();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeShort(this.requestId);
        buf.writeUtf(saveRequestable(this.provider));
        this.provider.writeRequest(this.value, buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context context = sup.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
                if (player != null) {
                    try {
                        ModMessages.sendToClientPlayer(new RequestDataPacket<>(this.requestId, this.provider, this.provider.pack(this.value, player)), player);
                    } catch (Exception e) {
                        KapLibMod.LOGGER.warn(Markers.REQUESTS, "unable to handle Request Packet of provider '{}': {}", ExtraRegistries.REQUESTABLES.getKey(this.provider), e.getMessage());
                    }
                }
        });
    }
}
