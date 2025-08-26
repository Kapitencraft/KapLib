package net.kapitencraft.kap_lib.inventory.menu;

import net.kapitencraft.kap_lib.inventory.wrapper.SlotWrapper;
import net.kapitencraft.kap_lib.mixin.duck.inventory.InventoryPageReader;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.UnaryOperator;

public class SlotAdder {
    private final UnaryOperator<Slot> adder;
    private final InventoryPageReader reader;
    private int pageIndex;

    public SlotAdder(UnaryOperator<Slot> adder, InventoryPageReader reader) {
        this.adder = adder;
        this.reader = reader;
    }

    public void addSlot(Slot in) {
        this.adder.apply(new SlotWrapper(reader, pageIndex, in));
    }

    /**
     * for internal use only. updates the page index for slot use
     */
    @ApiStatus.Internal
    public void updateSlotIndex(int index) {
        this.pageIndex = index;
    }
}
