package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DisplayTotemActivationPacket(ItemStack toDisplay, int entityId) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, DisplayTotemActivationPacket> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, DisplayTotemActivationPacket::toDisplay,
            ByteBufCodecs.INT, DisplayTotemActivationPacket::entityId,
            DisplayTotemActivationPacket::new
    );
    public static final Type<DisplayTotemActivationPacket> TYPE = new Type<>(KapLibMod.res("display_totem"));

    public void handle(IPayloadContext context) {
        context.enqueueWork(()-> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = level.getEntity(entityId);
                if (entity == null) return;
                level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
                Minecraft.getInstance().gameRenderer.displayItemActivation(toDisplay);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
