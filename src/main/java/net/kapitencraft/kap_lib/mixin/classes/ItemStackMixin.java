package net.kapitencraft.kap_lib.mixin.classes;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.enchantments.abstracts.StatBoostEnchantment;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.mixin.duck.IItemStackSelf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public class ItemStackMixin implements IItemStackSelf {

    @Inject(method = "getAttributeModifiers", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getAttributeModifiers(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/Multimap;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addInternalModifiers(EquipmentSlot pSlot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir, Multimap<Attribute, AttributeModifier> modifiers) {
        AttributeHelper.AttributeBuilder builder = new AttributeHelper.AttributeBuilder(modifiers);
        builder.merge(StatBoostEnchantment.getAllModifiers(self(), pSlot));
    }
}
