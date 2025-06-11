package net.kapitencraft.kap_lib.inventory.wrapper;

import net.kapitencraft.kap_lib.mixin.duck.inventory.InventoryPageReader;
import net.minecraft.world.inventory.Slot;

public class InventorySlotWrapper extends SlotWrapper {
    public InventorySlotWrapper(InventoryPageReader getter, Slot wrapped) {
        super(getter, 0, wrapped);
    }

    @Override
    public boolean isActive() {
        return getter.getPage().withInventory();
    }
}
