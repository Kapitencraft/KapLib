package net.kapitencraft.kap_lib.io.network.S2C.capability;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.inventory.wearable.Wearables;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SyncWearablesToPlayerPacket(int playerId, List<ItemStack> list) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncWearablesToPlayerPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncWearablesToPlayerPacket::playerId,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncWearablesToPlayerPacket::list,
            SyncWearablesToPlayerPacket::new
    );

    public static final Type<SyncWearablesToPlayerPacket> TYPE = new Type<>(KapLibMod.res("sync_wearables"));

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> {
            if (Minecraft.getInstance().level.getEntity(playerId) instanceof LivingEntity living) {
                Wearables.get(living).copyFrom(list);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
