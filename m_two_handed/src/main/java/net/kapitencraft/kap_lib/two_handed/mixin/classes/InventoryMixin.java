package net.kapitencraft.kap_lib.two_handed.mixin.classes;

import net.kapitencraft.kap_lib.two_handed.TwoHandedModule;
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
public abstract class InventoryMixin {

    @Shadow
    public abstract ItemStack getSelected();

    @Shadow
    public int selected;

    @Shadow
    public abstract void placeItemBackInInventory(ItemStack stack);

    @Shadow
    @Final
    public NonNullList<ItemStack> offhand;

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void blockOffhandInsertIfApplicable(int index, ItemStack stack, CallbackInfo ci) {
        if (index == TwoHandedModule.OFFHAND_SLOT_ID && getSelected().has(THItemComponents.TWO_HANDED)) {
            ci.cancel();
        }
    }

    @Inject(method = "addResource(ILnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void blockOffhandAddIfApplicable(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (slot == TwoHandedModule.OFFHAND_SLOT_ID && getSelected().has(THItemComponents.TWO_HANDED)) {
            cir.setReturnValue(stack.getCount());
        }
    }
}
