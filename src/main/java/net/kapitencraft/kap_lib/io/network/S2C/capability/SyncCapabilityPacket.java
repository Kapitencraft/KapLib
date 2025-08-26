package net.kapitencraft.kap_lib.io.network.S2C.capability;

import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.kapitencraft.kap_lib.item.capability.AbstractCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;

public abstract class SyncCapabilityPacket<D, C extends AbstractCapability<D>> implements SimplePacket {
    protected final List<D> data;

    protected SyncCapabilityPacket(List<D> data) {
        this.data = data;
    }

    public SyncCapabilityPacket(FriendlyByteBuf buf) {
        this.data = buf.readList(getReader());
    }

    protected abstract Capability<C> getCapability();

    protected abstract FriendlyByteBuf.Reader<D> getReader();
    protected abstract FriendlyByteBuf.Writer<D> getWriter();

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeCollection(data, getWriter());
    }

    public void updateCapability(ItemStack stack, D data) {
        stack.getCapability(getCapability()).ifPresent(c -> c.copyFrom(data));
    }
}
