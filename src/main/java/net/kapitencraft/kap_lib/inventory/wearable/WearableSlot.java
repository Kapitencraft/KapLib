package net.kapitencraft.kap_lib.inventory.wearable;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class WearableSlot {
    public static final StreamCodec<RegistryFriendlyByteBuf, WearableSlot> STREAM_CODEC = ByteBufCodecs.registry(ExtraRegistries.Keys.WEARABLE_SLOTS);

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

    public boolean is(Holder<WearableSlot> other) {
        return this == other.value();
    }

    public int getSlotIndex() {
        return ExtraRegistries.WEARABLE_SLOTS.getId(this);
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