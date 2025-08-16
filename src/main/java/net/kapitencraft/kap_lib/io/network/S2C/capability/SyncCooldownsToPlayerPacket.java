package net.kapitencraft.kap_lib.io.network.S2C.capability;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncCooldownsToPlayerPacket(int playerId, Map<Cooldown, Integer> data) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCooldownsToPlayerPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncCooldownsToPlayerPacket::playerId,
            ByteBufCodecs.map(HashMap::new, Cooldown.STREAM_CODEC, ByteBufCodecs.INT), SyncCooldownsToPlayerPacket::data,
            SyncCooldownsToPlayerPacket::new
    );
    public static final Type<SyncCooldownsToPlayerPacket> TYPE = new Type<>(KapLibMod.res("sync_cooldowns"));

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().level.getEntity(playerId) instanceof LivingEntity living) {
                Cooldowns.get(living).loadData(data);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
