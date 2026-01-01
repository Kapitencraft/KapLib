package net.kapitencraft.kap_lib.enchantment.mixin.classes.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.enchantment.extras.EnchantmentDescriptionManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
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
}
