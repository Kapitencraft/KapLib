package net.kapitencraft.kap_lib.client.enchantment_color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record EnchantmentColor(String name, List<Enchantment> elements, List<EnchantmentGroup> groups,
                               @Nullable LevelRange levelRange, Style targetStyle) {
    static final Codec<EnchantmentColor> CODEC = RecordCodecBuilder.create(enchantmentColorInstance -> enchantmentColorInstance.group(
            Codec.STRING.fieldOf("name").forGetter(EnchantmentColor::name),
            ForgeRegistries.ENCHANTMENTS.getCodec().listOf().optionalFieldOf("elements", List.of()).forGetter(EnchantmentColor::elements),
            EnchantmentGroup.CODEC.listOf().optionalFieldOf("groups", List.of()).forGetter(EnchantmentColor::groups),
            LevelRange.CODEC.optionalFieldOf("levelRange").forGetter(c -> Optional.ofNullable(c.levelRange)),
            ExtraCodecs.EFFECT_SERIALIZING_STYLE.fieldOf("style").forGetter(EnchantmentColor::targetStyle)
    ).apply(enchantmentColorInstance, EnchantmentColor::fromCodec));

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static EnchantmentColor fromCodec(String name, List<Enchantment> elements, List<EnchantmentGroup> groups, Optional<LevelRange> levelRange, Style targetStyle) {
        return EnchantmentColor.create(name, elements, groups, levelRange.orElse(null), targetStyle);
    }

    public static EnchantmentColor create(String name, List<Enchantment> elements, List<EnchantmentGroup> groups, @Nullable LevelRange range, Style targetStyle) {
        return new EnchantmentColor(name, new ArrayList<>(elements), new ArrayList<>(groups), range, targetStyle);
    }

    /**
     * gets the style for this color if both parameters match
     * @param enchantment the enchantment to query
     * @param level the level to query
     * @return the target style, or null, if the enchantment & level do not match
     */
    public Style getStyleForEnchantment(Enchantment enchantment, int level) {
        if (!this.groups.isEmpty() && this.groups.stream().noneMatch(g -> g.is(enchantment)))
            return null;
        if (!this.elements.isEmpty() && !this.elements.contains(enchantment))
            return null;
        if (this.levelRange != null && !this.levelRange.test(level, enchantment.getMaxLevel()))
            return null;
        return this.targetStyle;
    }
}
