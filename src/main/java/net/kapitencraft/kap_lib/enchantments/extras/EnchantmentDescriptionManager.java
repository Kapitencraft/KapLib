package net.kapitencraft.kap_lib.enchantments.extras;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.enchantment_color.EnchantmentColorManager;
import net.kapitencraft.kap_lib.client.glyph.enchantment_applicable.EnchantmentApplicableAllocator;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.enchantments.abstracts.ModEnchantment;
import net.kapitencraft.kap_lib.event.custom.client.RegisterEnchantmentApplicableCharsEvent;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;


@EventBusSubscriber(Dist.CLIENT)
public class EnchantmentDescriptionManager {

    private static final Style INFO_STYLE = Style.EMPTY.withColor(ChatFormatting.WHITE).withBold(false).withStrikethrough(false).withItalic(false).withObfuscated(false).withUnderlined(false);

    private static final ResourceLocation INFO_FONT_LOCATION = KapLibMod.res("enchantment_info"),
            APPLICABLE_FONT_LOCATION = KapLibMod.res("enchantment_applicable");


    public static void addTooltip(Consumer<Component> tooltips, Holder<Enchantment> holder, int level) {
        Enchantment ench = holder.value();
        MutableComponent component = Component.empty();

        component.append(ench.description())
                .append(CommonComponents.SPACE)
                .append(Component.translatable("enchantment.level." + level))
                .withStyle(MiscHelper.nonNullOr(EnchantmentColorManager.getStyle(holder, level), Style.EMPTY));
        if (true || fromBook(Items.DIAMOND_AXE)) {
            if (ClientModConfig.showObtainDisplay()) {
                component.append(CommonComponents.SPACE);
                component.append(
                        Component.literal(addObtainDisplay(holder))
                                .withStyle(INFO_STYLE.withFont(INFO_FONT_LOCATION))
                );
            }
            if (ClientModConfig.showApplyDisplay()) {
                component.append(CommonComponents.SPACE);
                component.append(
                        Component.literal(getApplicable(ench))
                                .withStyle(INFO_STYLE.withFont(APPLICABLE_FONT_LOCATION))
                );
            }
        }
        tooltips.accept(component);
        //if (Screen.hasShiftDown()) EnchantmentDescriptionManager.addTooltipForEnchant(tooltips, ench, player, level);
        //ClientHelper.addReqContent(tooltips, RegistryReqType.ENCHANTMENT, ench, player);
    }

    public static boolean fromBook(Item item) {
        return item instanceof EnchantedBookItem;
    }

    private static final char NO_TRADING = '\uF000', TREASURE = '\uF001';

    public static String addObtainDisplay(Holder<Enchantment> enchantment) {
        String s = "";
        if (enchantment.is(EnchantmentTags.TREASURE)) s += TREASURE;
        if (!enchantment.is(EnchantmentTags.TRADEABLE)) s += NO_TRADING;
        return s;
    }

    public static void addTooltipForEnchant(List<Component> list, Holder<Enchantment> enchantment, Player player, int level) {
        list.addAll(getDescription(enchantment, level));
    }

    public static List<Component> getDescription(Holder<Enchantment> ench, int level) {
        Object[] objects = ench instanceof ModEnchantment modEnchantment ? modEnchantment.getDescriptionMods(level) : new Object[]{level};
        Stream<String> stream = Arrays.stream(objects).map(String::valueOf);
        return TextHelper.getDescriptionOrEmpty(Util.makeDescriptionId("enchantment", ench.getKey().location()), component -> component.withStyle(ChatFormatting.DARK_GRAY), stream.map(TextHelper::wrapInRed).toArray());
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
        addItem(Items.DIAMOND_AXE);
        addItem(Items.DIAMOND_HOE);
        addItem(Items.BOW);
        addItem(Items.CROSSBOW, ResourceLocation.withDefaultNamespace("item/crossbow_standby"));
        addItem(Items.ELYTRA);
        addItem(Items.SHEARS);
        addItem(Items.TRIDENT);
        addItem(Items.FISHING_ROD);

        NeoForge.EVENT_BUS.post(new RegisterEnchantmentApplicableCharsEvent(EnchantmentDescriptionManager::addItem, EnchantmentDescriptionManager::addItem));
    }


    private static String getApplicable(Enchantment enchantment) {
        if (applicableMap.isEmpty()) initApplication(); //lazy init
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

    //endregion
}