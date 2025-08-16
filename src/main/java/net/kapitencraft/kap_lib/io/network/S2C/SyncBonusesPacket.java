package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncBonusesPacket(BonusManager.Data data) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBonusesPacket> CODEC =
            BonusManager.Data.STREAM_CODEC.map(SyncBonusesPacket::new, SyncBonusesPacket::data);

    public static final Type<SyncBonusesPacket> TYPE  = new Type<>(KapLibMod.res("sync_bonuses"));

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> BonusManager.createFromData(this.data));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
