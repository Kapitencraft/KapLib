package net.kapitencraft.kap_lib.inventory_page.page.crafting;

import net.kapitencraft.kap_lib.inventory_page.menu.SlotAdder;
import net.kapitencraft.kap_lib.inventory_page.page.InventoryPage;
import net.kapitencraft.kap_lib.inventory_page.registry.VanillaInventoryPages;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class CraftingPage extends InventoryPage {
    private static final ItemStack SYMBOL = new ItemStack(Items.CHEST);

    public CraftingPage(Player ignored, SlotAdder adder) {
        super(VanillaInventoryPages.CRAFTING.get());
    }

    @Override
    public @NotNull ItemStack symbol() {
        return SYMBOL;
    }

    @Override
    public boolean withInventory() {
        return true;
    }
}
