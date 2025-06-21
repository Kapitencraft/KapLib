package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.inventory.menu.SlotAdder;
import net.kapitencraft.kap_lib.inventory.page.InventoryPage;
import net.kapitencraft.kap_lib.inventory.page.InventoryPageType;
import net.kapitencraft.kap_lib.inventory.wrapper.InventorySlotWrapper;
import net.kapitencraft.kap_lib.inventory.wrapper.SlotWrapper;
import net.kapitencraft.kap_lib.mixin.duck.inventory.InventoryPageIO;
import net.kapitencraft.kap_lib.mixin.duck.inventory.InventoryPageReader;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractContainerMenu implements InventoryPageIO {
    @Unique
    private InventoryPage[] pages;
    @Unique
    private int openPage;

    protected InventoryMenuMixin(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Inject(method = "<init>", at = @At("TAIL"))
    private void loadPages(Inventory pPlayerInventory, boolean pActive, Player pOwner, CallbackInfo ci) {
        Collection<InventoryPageType<?>> pageTypes = ExtraRegistries.INVENTORY_PAGES.getValues();
        InventoryPage[] pages = new InventoryPage[pageTypes.size()];
        SlotAdder adder = new SlotAdder(s -> addSlot(s), this); //DO NOT convert to method reference as that will load the mixin class, crashing the game
        int i = 0;
        for (InventoryPageType<?> pageType : pageTypes) {
            adder.updateSlotIndex(i);
            pages[i] = pageType.create(pOwner, adder);
            i++;
        }
        this.pages = pages;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/InventoryMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;"))
    private Slot wrapSlots(InventoryMenu instance, Slot slot) {
        if (slot.container instanceof Inventory && slot.getSlotIndex() < 36) return this.addSlot(new InventorySlotWrapper(this, slot));
        else return this.addSlot(new SlotWrapper(this, 0, slot));
    }

    @Override
    public int getPageIndex() {
        return openPage;
    }

    @Override
    public InventoryPage[] getPages() {
        return pages;
    }

    @Override
    public void setPage(int page) {
        this.openPage = page;
    }
}
