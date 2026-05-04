package net.kapitencraft.kap_lib.attribute.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.kapitencraft.kap_lib.attribute.BaseAttributeLocations;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Items.class)
public class ItemsMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "NEW", target = "Lnet/minecraft/world/item/BowItem;"))
    private static BowItem addAttributes(Item.Properties properties, Operation<BowItem> operation) {
        return operation.call(properties.attributes(ItemAttributeModifiers.builder()
                        .add(ExtraAttributes.RANGED_DAMAGE, new AttributeModifier(BaseAttributeLocations.RANGED_DAMAGE, 2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build()));
    }
}
