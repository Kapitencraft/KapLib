package net.kapitencraft.kap_lib.inventory.wearable;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.data_gen.tags.ModTagsProvider;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.Map;
import java.util.function.Supplier;

public class WearableSlot {
    public static ResourceLocation TAG_KEY_SLAVE_MAP = KapLibMod.res("tag_keys");


    private final int xPos, yPos;

    public WearableSlot(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
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
}