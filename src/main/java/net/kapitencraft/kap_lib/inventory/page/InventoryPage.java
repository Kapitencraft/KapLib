package net.kapitencraft.kap_lib.inventory.page;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryPage {
    private final InventoryPageType<?> type;

    protected InventoryPage(InventoryPageType<?> type) {
        this.type = type;
    }

    public abstract @NotNull ItemStack symbol();

    /**
     * @param player the player that is viewing the pages
     * @return whether this page should be visible and to be interacted with
     */
    public boolean isVisible(Player player) {
        return true;
    }

    /**
     * @return whether the inventory slots should be rendered
     */
    public abstract boolean withInventory();

    public InventoryPageType<?> getType() {
        return type;
    }

    ResourceLocation TAB_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

    public @NotNull ResourceLocation tabLocation() {
        return TAB_LOCATION;
    }
}
