package net.kapitencraft.kap_lib.mixin.classes.client;

import net.kapitencraft.kap_lib.enchantments.extras.EnchantmentDescriptionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {

    @Redirect(method = "appendHoverText", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V"))
    private void addEnchantmentDescriptions(List<Component> tooltip, ListTag stored, ItemStack pStack) {
        EnchantmentDescriptionManager.addAllTooltips(pStack, tooltip, stored, Minecraft.getInstance().player);
    }
}
