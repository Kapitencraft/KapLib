package net.kapitencraft.kap_lib.inventory_page.mixin.duck.inventory;

import net.kapitencraft.kap_lib.inventory_page.page.InventoryPage;

public interface InventoryPageReader {

    int getPageIndex();

    InventoryPage[] getPages();

    default InventoryPage getPage() {
        return getPages()[getPageIndex()];
    }
}
