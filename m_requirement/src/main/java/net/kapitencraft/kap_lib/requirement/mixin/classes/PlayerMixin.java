package net.kapitencraft.kap_lib.requirement.mixin.classes;

import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.kapitencraft.kap_lib.requirement.type.RegistryReqType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin implements MixinSelfProvider<Player> {

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    public void checkGlideAllowed(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = self().getItemBySlot(EquipmentSlot.CHEST);
        if (!RequirementManager.instance.meetsRequirements(RegistryReqType.ITEM, stack.getItem(), self())) {
            cir.setReturnValue(false);
        }
    }
}
