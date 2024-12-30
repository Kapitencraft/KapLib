package net.kapitencraft.kap_lib.io.network.request;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import org.slf4j.Marker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RequestHandler {
    private final Map<Short, Consumer<?>> requests = new HashMap<>();
    private short nextEmpty = 0;

    private final Consumer<RequestPacket<?, ?>> packetDistributor;

    public RequestHandler(Consumer<RequestPacket<?, ?>> packetDistributor) {
        this.packetDistributor = packetDistributor;
    }

    public synchronized <T, K> void createRequest(IRequestable<T, K> requestable, K value, Consumer<T> consumer) {
        try {
            RequestPacket<T, K> packet = new RequestPacket<T, K>(nextEmpty, requestable, value);
            packetDistributor.accept(packet);
            requests.put(nextEmpty, consumer);
            nextEmpty = getNextEmpty(nextEmpty);
        } catch (Exception e) {
            KapLibMod.LOGGER.warn((Marker) Markers.REQUESTS, "unable to send request of type '{}': {}", ExtraRegistries.REQUESTABLES.getKey(requestable), e.getMessage());
        }
    }

    private short getNextEmpty(short in) {
        while (!requests.containsKey(in)) {
            in++;
        }
        return in;
    }

    public synchronized <T, K> void accordPackageReceive(RequestDataPacket<T, K> packet) {
        try {
            short key = packet.getId();
            Consumer<T> consumer = (Consumer<T>) requests.get(key);
            consumer.accept(packet.getData());
            requests.remove(key);
            nextEmpty = key;
        } catch (Exception e) {
            KapLibMod.LOGGER.warn((Marker) Markers.REQUESTS, "error receiving packet: {}", e.getMessage());
        }
    }
}