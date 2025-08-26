package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.enchantments.abstracts.IUltimateEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.extensions.IForgeEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements IForgeEnchantment {

    @Inject(method = "checkCompatibility", at = @At("HEAD"), cancellable = true)
    private void addUltimateSupport(Enchantment pOther, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IUltimateEnchantment && pOther instanceof IUltimateEnchantment) cir.setReturnValue(false);
    }

    @Inject(method = "isTreasureOnly", at = @At("HEAD"), cancellable = true)
    private void ultimateTreasure(CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof IUltimateEnchantment) cir.setReturnValue(true);
    }
}
