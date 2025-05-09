package net.kapitencraft.kap_lib.mixin.classes;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.enchantments.abstracts.StatBoostEnchantment;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /**m
     * adds the applied stat boost enchantments' attribute modifiers
     */
    @Redirect(method = "getAttributeModifiers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;"))
    private Multimap<Attribute, AttributeModifier> addInternalModifiers(Item instance, EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = instance.getAttributeModifiers(slot, stack);
        AttributeHelper.AttributeBuilder builder = new AttributeHelper.AttributeBuilder(attributes);
        builder.merge(StatBoostEnchantment.getAllModifiers(stack, slot));
        return builder.build();
    }

    @Redirect(method = "isSameItemSameTags", at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    private static boolean ensureTagsEqual(Object a, Object b) {
        CompoundTag tagA = (CompoundTag) a;
        CompoundTag tagB = (CompoundTag) b;
        return tagA == null || tagA.isEmpty() ? tagB == null || tagB.isEmpty() : Objects.equals(a, b);
    }
}
