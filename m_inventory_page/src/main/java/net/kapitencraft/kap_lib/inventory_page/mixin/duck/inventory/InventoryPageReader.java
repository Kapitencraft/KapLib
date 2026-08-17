package net.kapitencraft.kap_lib.inventory_page.mixin.duck.inventory;

import net.kapitencraft.kap_lib.inventory_page.page.InventoryPage;

public interface InventoryPageReader {

    default int getPageIndex() {
        return -1;
    }

    default InventoryPage[] getPages() {
        return new InventoryPage[0];
    }

    default InventoryPage getPage() {
        return getPages()[getPageIndex()];
    }
}
