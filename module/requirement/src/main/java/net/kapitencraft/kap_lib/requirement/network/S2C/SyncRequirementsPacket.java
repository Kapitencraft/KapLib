package net.kapitencraft.kap_lib.requirement.network.S2C;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record SyncRequirementsPacket(RequirementManager.Data data) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRequirementsPacket> STREAM_CODEC = RequirementManager.instance.dataStreamCodec.map(SyncRequirementsPacket::new, SyncRequirementsPacket::data);
    public static final Type<SyncRequirementsPacket> TYPE = new Type<>(LibConstants.res("sync_requirements"));

    public static SyncRequirementsPacket create() {
        return new SyncRequirementsPacket(RequirementManager.createData());
    }

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> RequirementManager.copyData(data));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}