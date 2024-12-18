package net.kapitencraft.kap_lib.enchantments.extras;

import net.kapitencraft.kap_lib.enchantments.abstracts.IUltimateEnchantment;
import net.kapitencraft.kap_lib.enchantments.abstracts.ModEnchantment;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.registry.GlyphEffects;
import net.kapitencraft.kap_lib.requirements.RequirementType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Mod.EventBusSubscriber()
public class EnchantmentDescriptionManager {

    public static void addAllTooltips(ItemStack stack, List<Component> tooltips, ListTag pStoredEnchantments, Player player) {
        if (pStoredEnchantments.isEmpty()) return;
        if (!Screen.hasShiftDown()) tooltips.add(Component.translatable("ench_desc.shift").withStyle(ChatFormatting.DARK_GRAY));
        for(int i = 0; i < pStoredEnchantments.size(); ++i) {
            CompoundTag compoundtag = pStoredEnchantments.getCompound(i);
            Optional.ofNullable(ForgeRegistries.ENCHANTMENTS.getValue(EnchantmentHelper.getEnchantmentId(compoundtag))).ifPresent((ench) -> {
                int level = EnchantmentHelper.getEnchantmentLevel(compoundtag);
                MutableComponent component = (MutableComponent) ench.getFullname(level);
                if (!(ench.isCurse())) {
                    if (ench instanceof IUltimateEnchantment) {
                        component.withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.BOLD);
                    } else if (level == ench.getMaxLevel()) {
                        component.withStyle(ChatFormatting.GOLD);
                    } else if (level > ench.getMaxLevel()) {
                        component.setStyle(MiscHelper.withSpecial(component.getStyle(), GlyphEffects.RAINBOW.get()));
                    }
                }
                tooltips.add(component);
                if (Screen.hasShiftDown()) EnchantmentDescriptionManager.addTooltipForEnchant(stack, tooltips, ench, player);
                ClientHelper.addReqContent(tooltips::add, RequirementType.ENCHANTMENT, ench, player);
            });
        }
    }

    public static void addTooltipForEnchant(ItemStack stack, List<Component> list, Enchantment enchantment, Player player) {
        if (fromBook(stack.getItem())) {
            if (!enchantment.isTradeable()) list.add(Component.translatable("ench_desc.not_tradeable").withStyle(ChatFormatting.YELLOW));
            if (enchantment.isTreasureOnly()) list.add(Component.translatable("ench_desc.treasure").withStyle(ChatFormatting.YELLOW));
        }
        List<Component> description = getDescription(stack, enchantment);
        if (description.isEmpty()) list.add(Component.translatable("ench_desc.missing").withStyle(ChatFormatting.DARK_GRAY));
        else list.addAll(description);
    }

    public static boolean fromBook(Item item) {
        return item instanceof EnchantedBookItem;
    }

    public static List<Component> getDescription(ItemStack stack, Enchantment ench) {
        int level = stack.getItem() instanceof EnchantedBookItem ? (EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(stack)).get(ench)) : EnchantmentHelper.getTagEnchantmentLevel(ench, stack);
        Object[] objects = ench instanceof ModEnchantment modEnchantment ? modEnchantment.getDescriptionMods(level) : new String[]{String.valueOf(level)};
        Stream<String> stream = Arrays.stream(objects).map(String::valueOf);
        return TextHelper.getDescriptionList(ench.getDescriptionId(), component -> component.withStyle(ChatFormatting.DARK_GRAY), stream.map(TextHelper::wrapInRed).toArray());
    }
}