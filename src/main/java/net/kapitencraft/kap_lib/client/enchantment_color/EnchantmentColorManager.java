package net.kapitencraft.kap_lib.client.enchantment_color;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.collection.DoubleMap;
import net.kapitencraft.kap_lib.enchantments.extras.EnchantmentDescriptionManager;
import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.GlyphEffects;
import net.kapitencraft.kap_lib.util.range.simple.IntegerNumberRange;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentColorManager {
    static Codec<EnchantmentColorManager> CODEC = EnchantmentColor.CODEC.listOf().xmap(EnchantmentColorManager::new, EnchantmentColorManager::getColors);
    public static final EnchantmentColorManager instance = load();

    private final List<EnchantmentColor> colors = new ArrayList<>();
    private final DoubleMap<Enchantment, Integer, Style> cache = DoubleMap.create();

    /**
     * a holder for the File all information is saved in
     */
    private static File PERSISTENT_FILE;

    public EnchantmentColorManager(List<EnchantmentColor> colors) {
        this.colors.addAll(colors);
    }

    private static @NotNull File getOrCreateFile() {
        if (PERSISTENT_FILE == null) {
            PERSISTENT_FILE = new File(KapLibMod.MAIN, "enchantment_colors_config.json");
        }
        return PERSISTENT_FILE;
    }

    private static EnchantmentColorManager createDefault() {
        return new EnchantmentColorManager(List.of(
                new EnchantmentColor(
                        I18n.get("enchantment_colors.curse"),
                        List.of(),
                        List.of(EnchantmentGroup.CURSE),
                        null,
                        Style.EMPTY.withColor(ChatFormatting.RED)
                ),
                new EnchantmentColor(
                        I18n.get("enchantment_colors.ultimate"),
                        List.of(),
                        List.of(EnchantmentGroup.ULTIMATE),
                        null,
                        Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE).withBold(true)
                ),
                new EnchantmentColor(
                        I18n.get("enchantment_colors.max_level"),
                        List.of(),
                        List.of(),
                        new LevelRange(0, 0, true),
                        Style.EMPTY.withColor(ChatFormatting.GOLD)
                ),
                new EnchantmentColor(
                        I18n.get("enchantment_colors.over_level"),
                        List.of(),
                        List.of(),
                        new LevelRange(1, Integer.MAX_VALUE, true),
                        MiscHelper.withSpecial(Style.EMPTY, GlyphEffects.RAINBOW)
                )
        ));
    }

    public static EnchantmentColorManager load() {
        return IOHelper.loadFile(getOrCreateFile(), CODEC, EnchantmentColorManager::createDefault);
    }

    private List<EnchantmentColor> getColors() {
        return colors;
    }

    public static Style getStyle(Enchantment enchantment, int level) {
        return instance.getStyleForInstance(enchantment, level);
    }

    private Style getStyleForInstance(Enchantment enchantment, int level) {
        Style style = this.cache.get(enchantment, level);
        if (style != null) return style;

        for (EnchantmentColor color : colors) {
            style = color.getStyleForEnchantment(enchantment, level);
            if (style != null) break;
        }
        this.cache.put(enchantment, level, style);
        return style;
    }

    @ApiStatus.Internal
    public void save(List<EnchantmentColor> colors) {
        if (!this.colors.equals(colors)) {
            this.colors.clear();
            this.colors.addAll(colors);
            IOHelper.saveFile(getOrCreateFile(), CODEC, this);
            this.cache.clear();
        }
    }

    List<EnchantmentColor> getAllColors() {
        return this.colors;
    }
}
