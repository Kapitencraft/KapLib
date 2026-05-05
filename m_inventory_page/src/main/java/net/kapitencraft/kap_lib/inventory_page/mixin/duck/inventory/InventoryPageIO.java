package net.kapitencraft.kap_lib.inventory_page.mixin.duck.inventory;

public interface InventoryPageIO extends InventoryPageReader, InventoryPageWriter {
    void cycle();
}
