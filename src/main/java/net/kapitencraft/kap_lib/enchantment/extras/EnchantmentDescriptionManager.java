package net.kapitencraft.kap_lib.enchantment.extras;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.TextHelper;
import net.kapitencraft.kap_lib.enchantment.client.enchantment_applicable.EnchantmentApplicableAllocator;
import net.kapitencraft.kap_lib.enchantment.config.EnchantmentClientModConfig;
import net.kapitencraft.kap_lib.enchantment.event.custom.RegisterEnchantmentApplicableCharsEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class EnchantmentDescriptionManager {

    private static final Style INFO_STYLE = Style.EMPTY.withColor(ChatFormatting.WHITE).withBold(false).withStrikethrough(false).withItalic(false).withObfuscated(false).withUnderlined(false);

    private static final ResourceLocation INFO_FONT_LOCATION = LibConstants.res("enchantment_info"),
            APPLICABLE_FONT_LOCATION = LibConstants.res("enchantment_applicable");

    public static void addTooltip(Consumer<Component> tooltips, Holder<Enchantment> holder) {
        if (Screen.hasShiftDown()) addTooltipForEnchant(tooltips, holder);
    }

    public static void addTooltipForEnchant(Consumer<Component> tooltipSink, Holder<Enchantment> enchantment) {
        getDescription(enchantment).forEach(tooltipSink);
    }

    public static List<Component> getDescription(Holder<Enchantment> ench) {
        return TextHelper.getDescriptionOrEmpty(
                Util.makeDescriptionId("enchantment", ench.getKey().location()), component -> component.withStyle(ChatFormatting.DARK_GRAY));
    }

    private static final char NO_TRADING = '\uF000', TREASURE = '\uF001';

    public static String addObtainDisplay(Holder<Enchantment> enchantment) {
        String s = "";
        if (enchantment.is(EnchantmentTags.TREASURE)) s += TREASURE;
        if (!enchantment.is(EnchantmentTags.TRADEABLE)) s += NO_TRADING;
        return s;
    }

    //region applicable display

    /**
     * the map with the test item as key and the character for display as value.
     * the characters font texture must be registered under the {@link #APPLICABLE_FONT_LOCATION}
     */
    private static final List<Pair<Item, Character>> applicableMap = new ArrayList<>();
    /**
     * cache for applicable display
     */
    private static final Map<Enchantment, String> applicableCache = new HashMap<>();

    private static void addItem(Item item, ResourceLocation location) {
        applicableMap.add(Pair.of(item, EnchantmentApplicableAllocator.getInstance().addEntry(location)));
    }

    private static void addItem(Item item) {
        addItem(item, BuiltInRegistries.ITEM.getKey(item).withPrefix("item/"));
    }

    public static void initApplication() {
        addItem(Items.DIAMOND_BOOTS);
        addItem(Items.DIAMOND_LEGGINGS);
        addItem(Items.DIAMOND_CHESTPLATE);
        addItem(Items.DIAMOND_HELMET);
        addItem(Items.DIAMOND_PICKAXE);
        addItem(Items.DIAMOND_SWORD);
        addItem(Items.DIAMOND_AXE);
        addItem(Items.DIAMOND_HOE);
        addItem(Items.BOW);
        addItem(Items.CROSSBOW, ResourceLocation.withDefaultNamespace("item/crossbow_standby"));
        addItem(Items.ELYTRA);
        addItem(Items.SHEARS);
        addItem(Items.MACE);
        addItem(Items.TRIDENT);
        addItem(Items.FISHING_ROD);

        NeoForge.EVENT_BUS.post(new RegisterEnchantmentApplicableCharsEvent(EnchantmentDescriptionManager::addItem, EnchantmentDescriptionManager::addItem));
    }

    private static String getApplicable(Enchantment enchantment) {
        if (applicableCache.containsKey(enchantment)) return applicableCache.get(enchantment);

        StringBuilder s = new StringBuilder();
        Enchantment.EnchantmentDefinition definition = enchantment.definition();
        for (Pair<Item, Character> test : applicableMap) {
            if (definition.supportedItems().contains(test.getFirst().builtInRegistryHolder())) {
                s.append(test.getSecond());
            }
        }
        String value = s.toString();
        applicableCache.put(enchantment, value);
        return value;
    }

    public static void reset() {
        applicableMap.clear();
        applicableCache.clear();
    }

    public static void appendInfoDisplay(MutableComponent component, Holder<Enchantment> enchantment) {
        Component space = CommonComponents.space().withStyle(Style.EMPTY.withStrikethrough(false).withUnderlined(false));
        if (EnchantmentClientModConfig.showObtainDisplay()) {
            String display = EnchantmentDescriptionManager.addObtainDisplay(enchantment);
            if (!display.isEmpty()) {
                component.append(space);
                component.append(
                        Component.literal(display)
                                .withStyle(INFO_STYLE.withFont(INFO_FONT_LOCATION))
                );
            }
        }
        if (EnchantmentClientModConfig.showApplyDisplay()) {
            String applicable = getApplicable(enchantment.value());
            if (!applicable.isEmpty()) {
                component.append(space);
                component.append(
                        Component.literal(applicable)
                                .withStyle(INFO_STYLE.withFont(APPLICABLE_FONT_LOCATION))
                );
            }
        }
    }
    //endregion
}