package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.core.config.ServerModConfig;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40))
    private int getAnvilCap(int constant) {
        return ServerModConfig.getAnvilLimit();
    }
}
