package net.kapitencraft.kap_lib.enchantment.mixin.classes;

import net.kapitencraft.kap_lib.enchantment.mixin.duck.ItemIsBookAccessor;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.TooltipContext.class)
public interface ItemTooltipContextMixin extends ItemIsBookAccessor {
    @Override
    default boolean isFromBook() {
        return false;
    }
}