package net.kapitencraft.kap_lib.enchantment.mixin.duck;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

public interface ItemIsBookAccessor {

    boolean isFromBook();

    static Item.TooltipContext of(Level level, ItemStack stack) {
        return level == null ? Item.TooltipContext.EMPTY : new Item.TooltipContext() {
            @Override
            public HolderLookup.Provider registries() {
                return level.registryAccess();
            }

            @Override
            public float tickRate() {
                return level.tickRateManager().tickrate();
            }

            @Override
            public @Nullable MapItemSavedData mapData(MapId mapId) {
                return level.getMapData(mapId);
            }

            @Override
            public boolean isFromBook() {
                return stack.is(Items.ENCHANTED_BOOK);
            }
        };
    }
}