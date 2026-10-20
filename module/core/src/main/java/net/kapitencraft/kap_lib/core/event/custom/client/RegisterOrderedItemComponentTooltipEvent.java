package net.kapitencraft.kap_lib.core.event.custom.client;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.util.InsertableLinkedOpenCustomHashSet;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public class RegisterOrderedItemComponentTooltipEvent extends Event implements IModBusEvent {

    private final InsertableLinkedOpenCustomHashSet<DataComponentType<? extends TooltipProvider>> entries;
    private DataComponentType<? extends TooltipProvider> afterAttributes;

    public RegisterOrderedItemComponentTooltipEvent(InsertableLinkedOpenCustomHashSet<DataComponentType<? extends TooltipProvider>> entries, DataComponentType<? extends TooltipProvider> afterAttributes) {
        this.entries = entries;
        this.afterAttributes = afterAttributes;
    }

    @ApiStatus.Internal
    public DataComponentType<? extends TooltipProvider> getAfterAttributes() {
        return afterAttributes;
    }

    public void insertAfter(Supplier<? extends DataComponentType<? extends TooltipProvider>> type, DataComponentType<? extends TooltipProvider> before) {
        entries.addAfter(before, type.get());
    }

    public void insertBefore(Supplier<? extends DataComponentType<? extends TooltipProvider>> type, DataComponentType<? extends TooltipProvider> after) {
        if (after == afterAttributes) {
            afterAttributes = type.get();
        }
        entries.addBefore(after, type.get());
    }

    public void insertLast(Supplier<? extends DataComponentType<? extends TooltipProvider>> type) {
        entries.addLast(type.get());
    }

    public void insertFirst(Supplier<? extends DataComponentType<? extends TooltipProvider>> type) {
        entries.addFirst(type.get());
    }
}