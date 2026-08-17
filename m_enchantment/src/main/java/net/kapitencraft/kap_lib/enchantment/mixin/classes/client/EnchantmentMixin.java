package net.kapitencraft.kap_lib.enchantment.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.enchantment.client.enchantment_color.EnchantmentColorManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @WrapOperation(method = "getFullname", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/ComponentUtils;mergeStyles(Lnet/minecraft/network/chat/MutableComponent;Lnet/minecraft/network/chat/Style;)Lnet/minecraft/network/chat/MutableComponent;"))
    private static MutableComponent overrideStyle(MutableComponent component, Style style, Operation<MutableComponent> original, @Local(argsOnly = true) Holder<Enchantment> enchantment, @Local(argsOnly = true) int level) {
        component = original.call(component, MiscHelper.nonNullOr(EnchantmentColorManager.getStyle(enchantment, level), Style.EMPTY.withColor(ChatFormatting.GRAY)));
        return component;
    }
}
