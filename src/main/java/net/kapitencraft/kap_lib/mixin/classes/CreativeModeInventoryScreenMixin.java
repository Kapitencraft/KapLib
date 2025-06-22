package net.kapitencraft.kap_lib.mixin.classes;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    @Redirect(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;size()I"))
    private int fixExtraSlots(NonNullList<ItemStack> instance) {
        return 46;
    }
}
