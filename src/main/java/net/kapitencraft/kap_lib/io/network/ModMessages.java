package net.kapitencraft.kap_lib.io.network;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.io.network.S2C.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";

    private static SimpleChannel PACKET_HANDLER;

    private static int messageID = 0;
    private static int id() {
        return messageID++;
    }

    public static <MSG> void sendToServer(MSG message) {
        PACKET_HANDLER.sendToServer(message);
    }

    public static <MSG> void sendToClientPlayer(MSG message, ServerPlayer player) {
        PACKET_HANDLER.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <MSG> void sendToAllConnectedPlayers(Function<ServerPlayer, MSG> provider, ServerLevel serverLevel) {
        serverLevel.getPlayers(serverPlayer -> true).forEach(serverPlayer -> ModMessages.sendToClientPlayer(provider.apply(serverPlayer), serverPlayer));
    }


    public static void register() {
        PACKET_HANDLER = NetworkRegistry.ChannelBuilder
                .named(KapLibMod.res("messages"))
                .networkProtocolVersion(()-> PROTOCOL_VERSION)
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        addMessage(SyncRequirementsPacket.class, NetworkDirection.PLAY_TO_CLIENT, SyncRequirementsPacket::new);
        addMessage(SyncBonusesPacket.class, NetworkDirection.PLAY_TO_CLIENT, SyncBonusesPacket::new);
        addMessage(DisplayTotemActivationPacket.class, NetworkDirection.PLAY_TO_CLIENT, DisplayTotemActivationPacket::new);
        addMessage(SendParticleAnimationPacket.class, NetworkDirection.PLAY_TO_CLIENT, SendParticleAnimationPacket::new);
        addMessage(SendTrackingShotPacket.class, NetworkDirection.PLAY_TO_CLIENT, SendTrackingShotPacket::new);
        addMessage(ActivateShakePacket.class, NetworkDirection.PLAY_TO_CLIENT, ActivateShakePacket::new);
    }


    private static <T extends SimplePacket> void addMessage(Class<T> tClass, NetworkDirection direction, Function<FriendlyByteBuf, T> decoder) {
        PACKET_HANDLER.messageBuilder(tClass, id(), direction)
                .decoder(decoder)
                .encoder(T::toBytes)
                .consumerMainThread(T::handle)
                .add();
    }

    private static <T extends SimplePacket> void addSimpleMessage(Class<T> packetClass, NetworkDirection direction, Supplier<T> supplier) {
        addMessage(packetClass, direction, buf -> supplier.get());
    }
}