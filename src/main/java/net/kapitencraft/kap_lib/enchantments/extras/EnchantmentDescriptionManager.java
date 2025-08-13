package net.kapitencraft.kap_lib.enchantments.extras;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.enchantment_color.EnchantmentColorManager;
import net.kapitencraft.kap_lib.client.glyph.enchantment_applicable.EnchantmentApplicableAllocator;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.enchantments.abstracts.ModEnchantment;
import net.kapitencraft.kap_lib.event.custom.client.RegisterEnchantmentApplicableCharsEvent;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.requirements.type.RegistryReqType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Stream;


@Mod.EventBusSubscriber(Dist.CLIENT)
public class EnchantmentDescriptionManager {

    private static final Style INFO_STYLE = Style.EMPTY.withColor(ChatFormatting.WHITE).withBold(false).withStrikethrough(false).withItalic(false).withObfuscated(false).withUnderlined(false);

    private static final ResourceLocation INFO_FONT_LOCATION = KapLibMod.res("enchantment_info"),
            APPLICABLE_FONT_LOCATION = KapLibMod.res("enchantment_applicable");


    public static void addAllTooltips(ItemStack stack, List<Component> tooltips, ListTag pStoredEnchantments, Player player) {
        if (pStoredEnchantments.isEmpty()) return;
        if (!Screen.hasShiftDown()) tooltips.add(Component.translatable("ench_desc.shift").withStyle(ChatFormatting.DARK_GRAY));
        for(int i = 0; i < pStoredEnchantments.size(); ++i) {
            CompoundTag compoundtag = pStoredEnchantments.getCompound(i);
            Optional.ofNullable(ForgeRegistries.ENCHANTMENTS.getValue(EnchantmentHelper.getEnchantmentId(compoundtag))).ifPresent((ench) -> {
                int level = EnchantmentHelper.getEnchantmentLevel(compoundtag);
                MutableComponent component = Component.empty();
                component.append(((MutableComponent) ench.getFullname(level)).withStyle(MiscHelper.nonNullOr(EnchantmentColorManager.getStyle(ench, level), Style.EMPTY)));
                if (fromBook(stack.getItem())) {
                    if (ClientModConfig.showObtainDisplay()) {
                        component.append(CommonComponents.SPACE);
                        component.append(
                                Component.literal(addObtainDisplay(ench))
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
                tooltips.add(component);
                if (Screen.hasShiftDown()) EnchantmentDescriptionManager.addTooltipForEnchant(tooltips, ench, player, level);
                ClientHelper.addReqContent(tooltips::add, RegistryReqType.ENCHANTMENT, ench, player);
            });
        }
    }

    private static final char NO_TRADING = '\uF000', TREASURE = '\uF001';

    public static String addObtainDisplay(Enchantment enchantment) {
        String s = "";
        if (enchantment.isTreasureOnly()) s += TREASURE;
        if (!enchantment.isTradeable()) s += NO_TRADING;
        return s;
    }

    public static void addTooltipForEnchant(List<Component> list, Enchantment enchantment, Player player, int level) {
        list.addAll(getDescription(enchantment, level));
    }

    public static boolean fromBook(Item item) {
        return item instanceof EnchantedBookItem;
    }

    public static List<Component> getDescription(Enchantment ench, int level) {
        Object[] objects = ench instanceof ModEnchantment modEnchantment ? modEnchantment.getDescriptionMods(level) : new Object[]{level};
        Stream<String> stream = Arrays.stream(objects).map(String::valueOf);
        return TextHelper.getDescriptionOrEmpty(ench.getDescriptionId(), component -> component.withStyle(ChatFormatting.DARK_GRAY), stream.map(TextHelper::wrapInRed).toArray());
    }

    //APPLICABLE DISPLAY START

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
        addItem(item, ForgeRegistries.ITEMS.getKey(item).withPrefix("item/"));
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
        addItem(Items.CROSSBOW);
        addItem(Items.ELYTRA);
        addItem(Items.SHEARS);
        addItem(Items.TRIDENT);
        addItem(Items.FISHING_ROD);

        MinecraftForge.EVENT_BUS.post(new RegisterEnchantmentApplicableCharsEvent(EnchantmentDescriptionManager::addItem, EnchantmentDescriptionManager::addItem));
    }


    private static String getApplicable(Enchantment enchantment) {
        if (applicableMap.isEmpty()) initApplication(); //lazy init
        if (applicableCache.containsKey(enchantment)) return applicableCache.get(enchantment);

        EnchantmentCategory category = enchantment.category;
        StringBuilder s = new StringBuilder();
        for (Pair<Item, Character> test : applicableMap) {
            if (category.canEnchant(test.getFirst())) s.append(test.getSecond());
        }
        String value = s.toString();
        applicableCache.put(enchantment, value);
        return value;
    }

    //APPLICABLE DISPLAY END
}