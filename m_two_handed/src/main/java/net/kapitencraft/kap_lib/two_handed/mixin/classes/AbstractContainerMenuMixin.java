package net.kapitencraft.kap_lib.two_handed.mixin.classes;

import net.kapitencraft.kap_lib.two_handed.TwoHandedModule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void checkOffhandSwap(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        if (clickType == ClickType.SWAP && TwoHandedModule.isTwoHanded(player.getMainHandItem())) {
            ci.cancel();
        }
    }
}
