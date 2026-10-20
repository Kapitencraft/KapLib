package net.kapitencraft.kap_lib.two_handed.mixin.classes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommonHooks.class)
public class CommonHooksMixin {

    @Redirect(method = "onPlayerAttackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack changeWeaponItem(Player instance) {
        return instance.getWeaponItem();
    }
}
