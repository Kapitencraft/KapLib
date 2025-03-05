package net.kapitencraft.kap_lib.enchantments.extras;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.enchantment_color.EnchantmentColorManager;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.enchantments.abstracts.ModEnchantment;
import net.kapitencraft.kap_lib.event.custom.client.RegisterEnchantmentApplicableCharsEvent;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.requirements.RequirementType;
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
                if (Screen.hasShiftDown()) EnchantmentDescriptionManager.addTooltipForEnchant(stack, tooltips, ench, player, level);
                ClientHelper.addReqContent(tooltips::add, RequirementType.ENCHANTMENT, ench, player);
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

    public static void addTooltipForEnchant(ItemStack stack, List<Component> list, Enchantment enchantment, Player player, int level) {
        List<Component> description = getDescription(stack, enchantment, level);
        if (description.isEmpty()) list.add(Component.translatable("ench_desc.missing").withStyle(ChatFormatting.DARK_GRAY));
        else list.addAll(description);
    }

    public static boolean fromBook(Item item) {
        return item instanceof EnchantedBookItem;
    }

    public static List<Component> getDescription(ItemStack stack, Enchantment ench, int level) {
        Object[] objects = ench instanceof ModEnchantment modEnchantment ? modEnchantment.getDescriptionMods(level) : new Object[]{level};
        Stream<String> stream = Arrays.stream(objects).map(String::valueOf);
        return TextHelper.getDescriptionList(ench.getDescriptionId(), component -> component.withStyle(ChatFormatting.DARK_GRAY), stream.map(TextHelper::wrapInRed).toArray());
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

    static {
        applicableMap.addAll(List.of(
                Pair.of(Items.DIAMOND_BOOTS, '\u0001'),
                Pair.of(Items.DIAMOND_LEGGINGS, '\u0002'),
                Pair.of(Items.DIAMOND_CHESTPLATE, '\u0003'),
                Pair.of(Items.DIAMOND_HELMET, '\u0004'),
                Pair.of(Items.DIAMOND_PICKAXE, '\u0005'),
                Pair.of(Items.DIAMOND_SWORD, '\u0006'),
                Pair.of(Items.DIAMOND_AXE, '\u0007'),
                Pair.of(Items.DIAMOND_HOE, '\u0008'),
                Pair.of(Items.BOW, '\u0009'),
                Pair.of(Items.CROSSBOW, '\u0010'),
                Pair.of(Items.ELYTRA, '\u0011'),
                Pair.of(Items.SHEARS, '\u0012'),
                Pair.of(Items.TRIDENT, '\u0013'),
                Pair.of(Items.FISHING_ROD, '\u0014'))
        );
        MinecraftForge.EVENT_BUS.post(new RegisterEnchantmentApplicableCharsEvent(applicableMap));
    }


    private static String getApplicable(Enchantment enchantment) {
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