package net.kapitencraft.kap_lib.inventory.wearable;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class WearableSlot {
    public static ResourceLocation TAG_KEY_SLAVE_MAP = KapLibMod.res("tag_keys");

    private final int xPos, yPos;
    private final @Nullable Pair<ResourceLocation, ResourceLocation> noItemIcon;

    /**
     * @param xPos the x position of the slot inside the equipment inventory page
     * @param yPos the y position of the slot inside the equipment inventory page
     * @param noItemIcon the location of the no-item texture visible if there's no item in the slot
     */
    public WearableSlot(int xPos, int yPos, @Nullable Pair<ResourceLocation, ResourceLocation> noItemIcon) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.noItemIcon = noItemIcon;
    }

    /**
     * overload for the other constructor without no-item texture
     */
    public WearableSlot(int xPos, int yPos) {
        this(xPos, yPos, null);
    }

    public boolean is(Supplier<WearableSlot> other) {
        return this == other.get();
    }

    @SuppressWarnings("unchecked")
    public TagKey<Item> getTypeKey() {
        return (TagKey<Item>) ExtraRegistries.WEARABLE_SLOTS.getSlaveMap(TAG_KEY_SLAVE_MAP, Map.class).get(this);
    }

    @SuppressWarnings("UnstableApiUsage")
    public int getSlotIndex() {
        return ((ForgeRegistry<WearableSlot>) ExtraRegistries.WEARABLE_SLOTS).getID(this);
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return noItemIcon;
    }
}