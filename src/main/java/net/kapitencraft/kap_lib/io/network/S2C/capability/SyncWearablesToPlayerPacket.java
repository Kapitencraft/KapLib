package net.kapitencraft.kap_lib.io.network.S2C.capability;

import net.kapitencraft.kap_lib.inventory.wearable.Wearables;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncWearablesToPlayerPacket implements SimplePacket {
    private final int playerId;
    private final List<ItemStack> stacks;

    public SyncWearablesToPlayerPacket(int playerId, List<ItemStack> stacks) {
        this.playerId = playerId;
        this.stacks = stacks;
    }

    public SyncWearablesToPlayerPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readInt();
        this.stacks = buf.readCollection(ArrayList::new, FriendlyByteBuf::readItem);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(playerId);
        buf.writeCollection(stacks, FriendlyByteBuf::writeItem);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level.getEntity(playerId) instanceof LivingEntity living) {
                Wearables.get(living).copyFrom(stacks);
            }
        });
    }
}
