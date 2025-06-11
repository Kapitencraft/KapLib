package net.kapitencraft.kap_lib.mixin.classes.client;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.collection.BiCollectors;
import net.kapitencraft.kap_lib.enchantments.extras.EnchantmentDescriptionManager;
import net.kapitencraft.kap_lib.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.inventory.wearable.IWearable;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.item.BaseAttributeUUIDs;
import net.kapitencraft.kap_lib.item.ExtendedItem;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.kapitencraft.kap_lib.item.modifier_display.ItemModifiersDisplayExtension;
import net.kapitencraft.kap_lib.item.modifier_display.ModifierDisplayManager;
import net.kapitencraft.kap_lib.mixin.duck.MixinSelfProvider;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PaintingVariantTags;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements MixinSelfProvider<ItemStack> {

    @Shadow public abstract boolean is(TagKey<Item> pTag);

    @Shadow public abstract Item getItem();

    @Shadow
    private static boolean shouldShowInTooltip(int pHideFlags, ItemStack.TooltipPart pPart) {
        return false;
    }

    @Shadow public abstract Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pSlot);

    @Shadow @Final public static DecimalFormat ATTRIBUTE_MODIFIER_FORMAT;

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
            List<ItemModifiersDisplayExtension> extensions = ModifierDisplayManager.getExtensions(pPlayer, self());
            for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                appendModifiersDisplay(list, pPlayer,
                        this.getAttributeModifiers(equipmentslot),
                        "item.modifiers." + equipmentslot.getName(),
                        extensions.stream()
                                .collect(CollectorHelper.toValueMappedStream(e -> e.getModifiers(equipmentslot)))
                                .collect(BiCollectors.toPairList())
                );
            }
            if (self().getItem() instanceof IWearable wearable) {
                for (Map.Entry<ResourceKey<WearableSlot>, WearableSlot> slotEntry : ExtraRegistries.WEARABLE_SLOTS.getEntries()) {
                    appendModifiersDisplay(list, pPlayer,
                            wearable.getModifiers(slotEntry.getValue(), self()),
                            "item.modifiers.wearable." + getWearableKey(slotEntry.getKey()),
                            List.of() //TODO add display extensions
                    );
                }
            }
        }
    }

    @Unique
    private static String getWearableKey(ResourceKey<WearableSlot> key) {
        ResourceLocation location = key.location();
        return location.getNamespace() + "." + location.getPath();
    }

    @Unique
    private void appendModifiersDisplay(List<Component> list, Player pPlayer, Multimap<Attribute, AttributeModifier> multimap, String translation, List<Pair<ItemModifiersDisplayExtension, Multimap<Attribute, AttributeModifier>>> extensionData) {
        if (!multimap.isEmpty()) {
            list.add(CommonComponents.EMPTY);
            list.add(Component.translatable(translation).withStyle(ChatFormatting.GRAY));

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

                MutableComponent c;
                if (flag) {
                    //Base Values
                    c = CommonComponents.space().append(Component.translatable("attribute.modifier.equals." + modifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN);
                } else if (d0 > 0.0D) {
                    c = Component.translatable("attribute.modifier.plus." + modifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.BLUE);
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    c = Component.translatable("attribute.modifier.take." + modifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.RED);
                } else continue;
                for (Pair<ItemModifiersDisplayExtension, Multimap<Attribute, AttributeModifier>> pair : extensionData) {
                    for (AttributeModifier attributeModifier : pair.getSecond().get(entry.getKey())) {
                        if (attributeModifier.getOperation() == modifier.getOperation()) {
                            c = c.append(CommonComponents.SPACE).append(pair.getFirst().createComponent(attributeModifier.getAmount()));
                        }
                    }
                }
                list.add(c);
            }
        }

    }

    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z", ordinal = 4))
    private boolean overwriteAttributeDisplay(int pHideFlags, ItemStack.TooltipPart pPart)  {
        return false;
    }
}