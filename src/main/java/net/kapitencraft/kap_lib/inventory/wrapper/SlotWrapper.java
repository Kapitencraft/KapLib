package net.kapitencraft.kap_lib.inventory.wrapper;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.mixin.duck.inventory.InventoryPageReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotWrapper extends Slot {
    protected final InventoryPageReader getter;
    private final int pageId;
    protected final Slot wrapped;

    public SlotWrapper(InventoryPageReader getter, int pageId, Slot wrapped) {
        super(wrapped.container, wrapped.getContainerSlot(), wrapped.x, wrapped.y);
        this.getter = getter;
        this.pageId = pageId;
        this.wrapped = wrapped;
    }

    @Override
    public boolean isActive() {
        return (pageId == getter.getPageIndex()) && wrapped.isActive();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack pStack) {
        return wrapped.mayPlace(pStack);
    }

    @Override
    public boolean mayPickup(@NotNull Player pPlayer) {
        return wrapped.mayPickup(pPlayer);
    }

    @Override
    public @Nullable Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return wrapped.getNoItemIcon();
    }

    @Override
    public void setByPlayer(ItemStack pStack) {
        this.wrapped.setByPlayer(pStack);
    }
}
