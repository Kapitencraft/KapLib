package net.kapitencraft.kap_lib.enchantment.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.enchantment.mixin.duck.ItemIsBookAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Screen.class)
public class ScreenMixin {

    @WrapOperation(method = "getTooltipFromItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$TooltipContext;of(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/Item$TooltipContext;"))
    private static Item.TooltipContext updateContext(Level level, Operation<Item.TooltipContext> original, @Local(argsOnly = true) ItemStack stack) {
        return ItemIsBookAccessor.of(level, stack);
    }
}
