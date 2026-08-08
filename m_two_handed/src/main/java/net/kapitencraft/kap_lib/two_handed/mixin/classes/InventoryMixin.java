package net.kapitencraft.kap_lib.two_handed.mixin.classes;

import net.kapitencraft.kap_lib.two_handed.TwoHandedModule;
import net.kapitencraft.kap_lib.two_handed.mixin.duck.InventoryClearOffhand;
import net.kapitencraft.kap_lib.two_handed.registry.THItemComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin implements InventoryClearOffhand {

    @Shadow
    public abstract ItemStack getSelected();

    //TODO fix voiding items when placing item into offhand slot while holding a two handed item
    @Inject(method = "addResource(ILnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void blockOffhandAddIfApplicable(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (slot == TwoHandedModule.OFFHAND_SLOT_ID && TwoHandedModule.isTwoHanded(getSelected())) {
            cir.setReturnValue(stack.getCount());
        }
    }

    @Inject(method = "swapPaint", at = @At("TAIL"))
    private void updateOffhand(double direction, CallbackInfo ci) {
        if (TwoHandedModule.isTwoHanded(getSelected())) {
            this.clearOffhand();
        }
    }
}
