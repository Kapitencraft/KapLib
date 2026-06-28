package net.kapitencraft.kap_lib.two_handed.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    @Nullable
    public HitResult hitResult;

    @WrapOperation(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack includeOffhandAttack(LocalPlayer instance, InteractionHand hand, Operation<ItemStack> original) {
        if (this.hitResult.getType() == HitResult.Type.ENTITY)
            return instance.getWeaponItem();
        return original.call(instance, hand);
    }

    @WrapOperation(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
    private void includeOffhandSwing(LocalPlayer instance, InteractionHand hand, Operation<Void> original, @Local ItemStack itemstack) {
        if (itemstack == instance.getMainHandItem())
            original.call(instance, hand);
        else
            original.call(instance, InteractionHand.OFF_HAND);
    }
}
