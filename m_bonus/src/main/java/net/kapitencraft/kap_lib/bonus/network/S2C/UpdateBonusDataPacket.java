package net.kapitencraft.kap_lib.bonus.network.S2C;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.bonus.BonusManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UpdateBonusDataPacket(ItemStack from, ItemStack to, EquipmentSlot slot, int entityId) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBonusDataPacket> CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, UpdateBonusDataPacket::from,
            ItemStack.OPTIONAL_STREAM_CODEC, UpdateBonusDataPacket::to,
            ExtraStreamCodecs.EQUIPMENT_SLOT, UpdateBonusDataPacket::slot,
            ByteBufCodecs.INT, UpdateBonusDataPacket::entityId,
            UpdateBonusDataPacket::new
    );

    public static final Type<UpdateBonusDataPacket> TYPE = new Type<>(LibConstants.res("update_bonus_data"));

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);
            if (entity instanceof LivingEntity living) {
                BonusManager.swapFrom(living, slot, to, from);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
