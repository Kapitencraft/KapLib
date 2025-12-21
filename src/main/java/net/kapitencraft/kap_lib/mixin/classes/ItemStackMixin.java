package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder, MixinSelfProvider<ItemStack> {

    /**
     * @author Kapitencraft
     * @reason extra rarities
     */
    @Overwrite
    public Rarity getRarity() {
        return MiscHelper.getFinalRarity(getOrDefault(DataComponents.RARITY, Rarity.COMMON), self());
    }
}
