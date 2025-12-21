package net.kapitencraft.kap_lib.core.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.core.config.ServerModConfig;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;get()I", ordinal = 1))
    private int disableAnvilCap(DataSlot instance, Operation<Integer> original) {
        if (ServerModConfig.disableAnvilLimit()) return 0;
        return original.call(instance);
    }
}
