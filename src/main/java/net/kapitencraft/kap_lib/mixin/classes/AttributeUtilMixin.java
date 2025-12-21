package net.kapitencraft.kap_lib.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.item.modifier_display.ModifierDisplayManager;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AttributeUtil.class)
public class AttributeUtilMixin {

    @WrapOperation(method = "applyTextFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/Attribute;toBaseComponent(DDZLnet/minecraft/world/item/TooltipFlag;)Lnet/minecraft/network/chat/MutableComponent;"))
    private static MutableComponent addAttributeExtensions(Attribute instance, double value, double entityBase, boolean merged, TooltipFlag tooltipFlag, Operation<MutableComponent> original, @Local(argsOnly = true) ItemStack stack) {
        MutableComponent component = original.call(instance, value, entityBase, merged, tooltipFlag);
        //ModifierDisplayManager.ExtensionData extensions = ModifierDisplayManager.getExtensions(stack);
        //extensions.equipmentProviders().forEach(); //TODO re-add extensions
        return component;
    }
}
