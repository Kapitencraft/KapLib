package net.kapitencraft.kap_lib.inventory_page.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.inventory_page.mixin.duck.inventory.InventoryPageIO;
import net.kapitencraft.kap_lib.inventory_page.mixin.duck.inventory.InventoryPageWriter;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {

    @Unique
    int originalTab;

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @WrapOperation(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;size()I"))
    private int fixExtraSlots(NonNullList<?> instance, Operation<Integer> original) {
        return 46;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void fixTabIssue(LocalPlayer player, FeatureFlagSet enabledFeatures, boolean displayOperatorCreativeTab, CallbackInfo ci) {
        InventoryPageIO inventoryPageIO = ((InventoryPageIO) player.inventoryMenu);
        this.originalTab = inventoryPageIO.getPageIndex();
        inventoryPageIO.setPage(0);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void resetOriginalTab(CallbackInfo ci) {
        ((InventoryPageWriter) Objects.requireNonNull(this.minecraft.player).inventoryMenu).setPage(originalTab);
    }
}
