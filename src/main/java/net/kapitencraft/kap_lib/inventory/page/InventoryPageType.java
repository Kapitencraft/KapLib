package net.kapitencraft.kap_lib.inventory.page;

import net.kapitencraft.kap_lib.inventory.menu.SlotAdder;
import net.minecraft.world.entity.player.Player;

public class InventoryPageType<T extends InventoryPage> {
    private final PageConstructor<T> constructor;

    public InventoryPageType(PageConstructor<T> constructor) {
        this.constructor = constructor;
    }

    public T create(Player player, SlotAdder adder) {
        return this.constructor.construct(player, adder);
    }

    public interface PageConstructor<T extends InventoryPage> {
        T construct(Player player, SlotAdder adder);
    }
}
