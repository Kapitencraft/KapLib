package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.config.ServerModConfig;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;get()I", ordinal = 1))
    private int disableAnvilCap(DataSlot instance) {
        if (ServerModConfig.disableAnvilLimit()) return 0;
        return instance.get();
    }
}
