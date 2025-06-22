package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateBonusDataPacket implements SimplePacket {
    private final ItemStack from, to;
    private final EquipmentSlot slot;
    private final int entityId;

    public UpdateBonusDataPacket(ItemStack from, ItemStack stack, EquipmentSlot slot, int entityId) {
        this.from = from;
        to = stack;
        this.slot = slot;
        this.entityId = entityId;
    }

    public UpdateBonusDataPacket(FriendlyByteBuf buf) {
        this(buf.readItem(), buf.readItem(), buf.readEnum(EquipmentSlot.class), buf.readInt());
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(from);
        buf.writeItem(to);
        buf.writeEnum(slot);
        buf.writeInt(entityId);
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);
            if (entity instanceof LivingEntity living) {
                BonusManager.swapFrom(living, slot, to, from);
            }
        });
        return true;
    }
}
