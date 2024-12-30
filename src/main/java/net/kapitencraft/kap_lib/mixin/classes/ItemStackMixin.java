package net.kapitencraft.kap_lib.mixin.classes;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.enchantments.abstracts.StatBoostEnchantment;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.mixin.duck.IItemStackSelf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IItemStackSelf {

    @Shadow public abstract void releaseUsing(Level pLevel, LivingEntity pLivingEntity, int pTimeLeft);

    /**
     * adds the applied stat boost enchantments' attribute modifiers
     */
    @Redirect(method = "getAttributeModifiers", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getAttributeModifiers(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/Multimap;"), remap = false)
    private Multimap<Attribute, AttributeModifier> addInternalModifiers(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributes) {
        AttributeHelper.AttributeBuilder builder = new AttributeHelper.AttributeBuilder(attributes);
        builder.merge(StatBoostEnchantment.getAllModifiers(stack, equipmentSlot));
        return ForgeHooks.getAttributeModifiers(stack, equipmentSlot, builder.build());
    }
}
