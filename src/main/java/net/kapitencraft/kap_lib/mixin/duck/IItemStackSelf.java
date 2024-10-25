package net.kapitencraft.kap_lib.mixin.duck;

import net.minecraft.world.item.ItemStack;

public interface IItemStackSelf {

    default ItemStack self() {
        return (ItemStack) (Object) this;
    }
}
