package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncRequirementsPacket(RequirementManager.Data data) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRequirementsPacket> STREAM_CODEC = RequirementManager.instance.dataStreamCodec.map(SyncRequirementsPacket::new, SyncRequirementsPacket::data);

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(()-> RequirementManager.copyData(data));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}