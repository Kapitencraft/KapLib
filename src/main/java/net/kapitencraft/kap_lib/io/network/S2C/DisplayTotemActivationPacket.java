package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DisplayTotemActivationPacket implements SimplePacket {
    private final ItemStack toDisplay;
    private final int entityId;

    public DisplayTotemActivationPacket(ItemStack toDisplay, int entityId) {
        this.toDisplay = toDisplay;
        this.entityId = entityId;
    }

    public DisplayTotemActivationPacket(FriendlyByteBuf buf) {
        this(buf.readItem(), buf.readInt());
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(toDisplay);
        buf.writeInt(entityId);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(()-> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = level.getEntity(entityId);
                if (entity == null) return;
                level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
                Minecraft.getInstance().gameRenderer.displayItemActivation(toDisplay);
            }
        });
    }
}
