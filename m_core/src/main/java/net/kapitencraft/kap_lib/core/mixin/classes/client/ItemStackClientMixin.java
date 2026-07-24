package net.kapitencraft.kap_lib.core.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.core.client.ItemComponentTooltipOrder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(value = ItemStack.class, priority = 0)
public class ItemStackClientMixin {

    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void overrideComponentTooltip(ItemStack instance, DataComponentType<? extends TooltipProvider> component, Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, Operation<Void> original) {
        if (component == DataComponents.JUKEBOX_PLAYABLE) {
            for (DataComponentType<? extends TooltipProvider> entry : ItemComponentTooltipOrder.getBeforeEntries()) {
                original.call(instance, entry, context, tooltipAdder, tooltipFlag);
            }
        } else if (component == DataComponents.UNBREAKABLE) {
            for (DataComponentType<? extends TooltipProvider> entry : ItemComponentTooltipOrder.getAfterEntries()) {
                original.call(instance, entry, context, tooltipAdder, tooltipFlag);
            }
        }
    }

}
