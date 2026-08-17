package net.kapitencraft.kap_lib.enchantment.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.enchantment.extras.EnchantmentDescriptionManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemEnchantments.class)
public abstract class AddEnchantmentTooltipMixin {

    @Inject(method = "addToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private void injectTranslations(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, CallbackInfo ci, @Local Holder<Enchantment> enchantmentHolder) {
        EnchantmentDescriptionManager.addTooltip(tooltipAdder, enchantmentHolder);
    }

    @WrapOperation(method = "addToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;"))
    private Component addEnchantmentMetaInfo(Holder<Enchantment> enchantment, int level, Operation<Component> original, @Local(argsOnly = true) Item.TooltipContext context) {
        MutableComponent m = (MutableComponent) original.call(enchantment, level);
        if (context.isFromBook())
            EnchantmentDescriptionManager.appendInfoDisplay(m, enchantment);
        return m;
    }
}
