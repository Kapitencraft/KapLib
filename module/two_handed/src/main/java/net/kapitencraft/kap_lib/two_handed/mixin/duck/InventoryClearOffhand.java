package net.kapitencraft.kap_lib.two_handed.mixin.duck;

import net.kapitencraft.kap_lib.two_handed.TwoHandedModule;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public interface InventoryClearOffhand {

    default void clearOffhand() {
        Inventory i = (Inventory) this;
        if (i.player instanceof ServerPlayer serverPlayer) {
            if (!i.offhand.getFirst().isEmpty()) {
                ItemStack stack = i.offhand.getFirst();
                i.offhand.set(0, ItemStack.EMPTY);
                i.placeItemBackInInventory(stack);
                serverPlayer.connection.send(
                        new ClientboundContainerSetSlotPacket(
                                -2, 0,
                                TwoHandedModule.OFFHAND_SLOT_ID,
                                ItemStack.EMPTY
                        )
                );
            }
        }
    }
}
