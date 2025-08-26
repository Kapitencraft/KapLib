package net.kapitencraft.kap_lib.mixin.duck.inventory;

import net.kapitencraft.kap_lib.inventory.page.InventoryPage;

public interface InventoryPageReader {

    int getPageIndex();

    InventoryPage[] getPages();

    default InventoryPage getPage() {
        return getPages()[getPageIndex()];
    }
}
