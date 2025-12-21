package net.kapitencraft.kap_lib.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.item.ExtendedItem;
import net.kapitencraft.kap_lib.bonus.BonusManager;
import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
import net.kapitencraft.kap_lib.core.tags.ExtraTags;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin implements MixinSelfProvider<ItemStack> {

    @Shadow public abstract boolean is(TagKey<Item> pTag);

    @Shadow public abstract Item getItem();

    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void tryUsePlayerAppend(Item instance, ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag, Operation<Void> original, @Local(argsOnly = true) Player player) {
        if (instance instanceof ExtendedItem extendedItem) extendedItem.appendHoverTextWithPlayer(stack, context, tooltipComponents, tooltipFlag, player);
        else original.call(instance, stack, context, tooltipComponents, tooltipFlag);
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/util/AttributeUtil;addAttributeTooltips(Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;Lnet/neoforged/neoforge/common/util/AttributeTooltipContext;)V"))
    private void addSetDisplay(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        list.addAll(BonusManager.getBonusDisplay(self(), player));
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", shift = At.Shift.AFTER))
    private void addHitEndermanDisplay(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        if (is(ExtraTags.Items.HITS_ENDERMAN)) list.add(Component.translatable("tooltip.can_hit_enderman"));
    }
}