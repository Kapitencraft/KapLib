package net.kapitencraft.kap_lib.io.network.S2C.capability;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.item.capability.AbstractCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SyncCapabilityToPlayerPacket<D, C extends AbstractCapability<D>> {
    private final List<D> data = new ArrayList<>();

    public void handle(IPayloadContext sup) {
        sup.enqueueWork(() -> {
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
    }

    public static <D, C extends AbstractCapability<D>, S extends SyncCapabilityToPlayerPacket<D, C>> S createPacket(ServerPlayer player, EntityCapability<C> capability, Function<List<D>, S> creator) {
        Inventory inventory = player.getInventory();
        List<D> slots = new ArrayList<>(inventory.getContainerSize());
        for (int[] i = new int[] {0}; i[0] < inventory.getContainerSize(); i[0]++) {

            inventory.getItem(i[0]).getCapability(capability).resolve().ifPresentOrElse(
                    c -> slots.add(i[0], c.getData()),
                    () -> slots.add(null)
            );
        }
        return creator.apply(slots);
    }
}
