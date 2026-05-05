package net.kapitencraft.kap_lib.requirement.mixin.classes;

import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.kapitencraft.kap_lib.requirement.type.RegistryReqType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public class FishingRodMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    public void spawnHook(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir, @Local(name = "itemstack") ItemStack itemstack) {
        ItemStack stack = player.getItemInHand(hand);
        if (!RequirementManager.instance.meetsRequirements(RegistryReqType.ITEM, stack.getItem(), player)) {
            cir.setReturnValue(InteractionResultHolder.fail(itemstack));
        }
    }
}
