package net.kapitencraft.kap_lib.io.network.S2C.capability;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.item.capability.AbstractCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SyncCapabilityToPlayerPacket<D, C extends AbstractCapability<D>> extends SyncCapabilityPacket<D, C> {
    protected SyncCapabilityToPlayerPacket(List<D> data) {
        super(data);
    }

    protected SyncCapabilityToPlayerPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer == null) return;
            Inventory inventory = localPlayer.getInventory();
            int s = 0;
            for (int i = 0; i < data.size(); i++) {
                D data = this.data.get(i);
                if (data != null) {
                    updateCapability(inventory.getItem(i), data);
                    s++;
                }
            }
            KapLibMod.LOGGER.info("synced {} Items", s);
        });
        return true;
    }

    public static <D, C extends AbstractCapability<D>, S extends SyncCapabilityToPlayerPacket<D, C>> S createPacket(ServerPlayer player, Capability<C> capability, Function<List<D>, S> creator) {
        Inventory inventory = player.getInventory();
        List<D> slots = new ArrayList<>(inventory.getContainerSize());
        for (int[] i = new int[] {0}; i[0] < inventory.getContainerSize(); i[0]++) {
            inventory.getItem(i[0]).getCapability(capability).ifPresent(c -> slots.set(i[0], c.getData()));
        }
        return creator.apply(slots);
    }
}
