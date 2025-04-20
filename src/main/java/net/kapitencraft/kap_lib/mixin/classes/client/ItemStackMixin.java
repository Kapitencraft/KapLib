package net.kapitencraft.kap_lib.mixin.classes.client;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.enchantments.extras.EnchantmentDescriptionManager;
import net.kapitencraft.kap_lib.item.BaseAttributeUUIDs;
import net.kapitencraft.kap_lib.item.ExtendedItem;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract boolean is(TagKey<Item> pTag);

    @Shadow public abstract Item getItem();

    @Shadow
    private static boolean shouldShowInTooltip(int pHideFlags, ItemStack.TooltipPart pPart) {
        return false;
    }

    @Shadow public abstract Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pSlot);

    @Shadow @Final public static DecimalFormat ATTRIBUTE_MODIFIER_FORMAT;

    private ItemStack self() {
        return (ItemStack) (Object) this;
    }

    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V"))
    private void appendEnchantmentNames(List<Component> pTooltipComponents, ListTag pStoredEnchantments, Player player) {
        EnchantmentDescriptionManager.addAllTooltips(self(), pTooltipComponents, pStoredEnchantments, player);
    }

    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void tryUsePlayerAppend(Item instance, ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced, Player viewing) {
        if (instance instanceof ExtendedItem extendedItem) extendedItem.appendHoverTextWithPlayer(pStack, pLevel, pTooltipComponents, pIsAdvanced, viewing);
        else instance.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 4), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addSetDisplay(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        list.addAll(BonusManager.getBonusDisplay(self(), pPlayer));
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addHitEndermanDisplay(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        if (is(ExtraTags.Items.HITS_ENDERMAN)) list.add(Component.translatable("tooltip.can_hit_enderman"));
    }

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 4), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addAttributeTooltip(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list, MutableComponent mutablecomponent, int j) {
        if (shouldShowInTooltip(j, ItemStack.TooltipPart.MODIFIERS)) {
            for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                Multimap<Attribute, AttributeModifier> multimap = this.getAttributeModifiers(equipmentslot);
                if (!multimap.isEmpty()) {
                    list.add(CommonComponents.EMPTY);
                    list.add(Component.translatable("item.modifiers." + equipmentslot.getName()).withStyle(ChatFormatting.GRAY));

                    for(Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
                        AttributeModifier modifier = entry.getValue();
                        double d0 = modifier.getAmount();
                        boolean flag = false;
                        if (pPlayer != null) {
                            if (modifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                                d0 += pPlayer.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                                d0 += EnchantmentHelper.getDamageBonus(self(), MobType.UNDEFINED);
                                flag = true;
                            } else if (modifier.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                                d0 += pPlayer.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                                flag = true;
                            } else if (BaseAttributeUUIDs.get(modifier.getId()) == entry.getKey()) {
                                d0 += pPlayer.getAttributeBaseValue(entry.getKey());
                                flag = true;
                            }
                        }

                        double d1;
                        if (modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && modifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                            if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                                d1 = d0 * 10.0D;
                            } else {
                                d1 = d0;
                            }
                        } else {
                            d1 = d0 * 100.0D;
                        }

                        if (flag) {
                            list.add(CommonComponents.space().append(Component.translatable("attribute.modifier.equals." + modifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                        } else if (d0 > 0.0D) {
                            list.add(Component.translatable("attribute.modifier.plus." + modifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                        } else if (d0 < 0.0D) {
                            d1 *= -1.0D;
                            list.add(Component.translatable("attribute.modifier.take." + modifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.RED));
                        }
                    }
                }
            }
        }
    }

    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 4))
    private boolean overwriteAttributeDisplay(int pHideFlags, ItemStack.TooltipPart pPart)  {
        return false;
    }
}