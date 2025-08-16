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

public record CooldownStartedPacket(Cooldown cooldown, int duration, int entityId) implements CustomPacketPayload {
    public static final  StreamCodec<RegistryFriendlyByteBuf, CooldownStartedPacket> CODEC = StreamCodec.composite(
            Cooldown.STREAM_CODEC, CooldownStartedPacket::cooldown,
            ByteBufCodecs.INT, CooldownStartedPacket::duration,
            ByteBufCodecs.INT, CooldownStartedPacket::entityId,
            CooldownStartedPacket::new
    );
    public static final Type<CooldownStartedPacket> TYPE = new Type<>(KapLibMod.res("cooldown_started"));

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> {
            Cooldowns.get((LivingEntity) Minecraft.getInstance().level.getEntity(entityId)).setCooldownTime(cooldown, duration);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
