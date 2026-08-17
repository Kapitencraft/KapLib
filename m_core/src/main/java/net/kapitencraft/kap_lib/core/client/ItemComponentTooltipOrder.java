package net.kapitencraft.kap_lib.core.client;

import net.kapitencraft.kap_lib.core.event.custom.client.RegisterOrderedItemComponentTooltipEvent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.common.util.InsertableLinkedOpenCustomHashSet;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public class ItemComponentTooltipOrder {
    private static DataComponentType<? extends TooltipProvider>[] BEFORE_ENTRIES,
            AFTER_ENTRIES;

    public static void refresh() {
        InsertableLinkedOpenCustomHashSet<DataComponentType<? extends TooltipProvider>> entries = new InsertableLinkedOpenCustomHashSet<>();
        entries.add(DataComponents.JUKEBOX_PLAYABLE);
        entries.add(DataComponents.TRIM);
        entries.add(DataComponents.STORED_ENCHANTMENTS);
        entries.add(DataComponents.ENCHANTMENTS);
        entries.add(DataComponents.DYED_COLOR);
        entries.add(DataComponents.LORE);
        entries.add(DataComponents.UNBREAKABLE);

        RegisterOrderedItemComponentTooltipEvent event = ModLoader.postEventWithReturn(new RegisterOrderedItemComponentTooltipEvent(entries, DataComponents.UNBREAKABLE));
        List<DataComponentType<? extends TooltipProvider>> values = entries.stream().toList();

        int afterAttributesIndex = values.indexOf(event.getAfterAttributes());
        if (afterAttributesIndex == -1) {
            throw new IllegalStateException("missing data component entry for ordering: " + event.getAfterAttributes());
        }

        DataComponentType<? extends TooltipProvider>[] before = new DataComponentType[afterAttributesIndex];
        for (int i = 0; i < afterAttributesIndex; i++) {
            before[i] = values.get(i);
        }
        BEFORE_ENTRIES = before;

        DataComponentType<? extends TooltipProvider>[] after = new DataComponentType[values.size() - afterAttributesIndex];
        for (int i = 0; i < values.size() - afterAttributesIndex; i++) {
            after[i] = values.get(i + afterAttributesIndex);
        }
        AFTER_ENTRIES = after;
    }

    @ApiStatus.Internal
    public static DataComponentType<? extends TooltipProvider>[] getBeforeEntries() {
        return BEFORE_ENTRIES;
    }

    @ApiStatus.Internal
    public static DataComponentType<? extends TooltipProvider>[] getAfterEntries() {
        return AFTER_ENTRIES;
    }
}
