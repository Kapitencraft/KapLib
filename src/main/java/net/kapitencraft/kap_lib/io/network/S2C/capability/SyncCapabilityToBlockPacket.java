package net.kapitencraft.kap_lib.io.network.S2C.capability;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.item.capability.AbstractCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public abstract class SyncCapabilityToBlockPacket<D, C extends AbstractCapability<D>> extends SyncCapabilityPacket<D, C> {
    private final BlockPos pos;

    protected SyncCapabilityToBlockPacket(List<D> data, BlockPos pos) {
        super(data);
        this.pos = pos;
    }

    public SyncCapabilityToBlockPacket(FriendlyByteBuf buf) {
        super(buf);
        this.pos = buf.readBlockPos();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        super.toBytes(buf);
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(()-> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity == null) throw new IllegalStateException("unable to sync block data: BE not found");
            int[] s = new int[1];
            entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
                for (int i = 0; i < iItemHandler.getSlots(); i++) {
                    D data = this.data.get(i);
                    if (data != null) {
                        updateCapability(iItemHandler.getStackInSlot(i), data);
                        s[0]++;
                    }
                }
            });
            KapLibMod.LOGGER.info("synced {} Items to block {}", s[0], level.getBlockState(pos).getBlock());
        });
    }
}
