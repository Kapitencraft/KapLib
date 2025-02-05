package net.kapitencraft.kap_lib.client.enchantment_color;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnchantmentColor {
    static final Codec<EnchantmentColor> CODEC = RecordCodecBuilder.create(enchantmentColorInstance -> enchantmentColorInstance.group(
            ExtraCodecs.COMPONENT.fieldOf("name").forGetter(EnchantmentColor::getName),
            ForgeRegistries.ENCHANTMENTS.getCodec().listOf().optionalFieldOf("elements", List.of()).forGetter(EnchantmentColor::getElements),
            EnchantmentGroup.CODEC.listOf().optionalFieldOf("groups", List.of()).forGetter(EnchantmentColor::getGroups),
            LevelRange.CODEC.optionalFieldOf("levelRange").forGetter(c -> Optional.ofNullable(c.levelRange)),
            Style.FORMATTING_CODEC.fieldOf("style").forGetter(EnchantmentColor::getTargetStyle)
    ).apply(enchantmentColorInstance, EnchantmentColor::fromCodec));

    private Component name;
    private final List<Enchantment> elements = new ArrayList<>();
    private final List<EnchantmentGroup> groups = new ArrayList<>();
    private @Nullable LevelRange levelRange;
    private boolean maxLevelRelative;
    private Style targetStyle;

    public EnchantmentColor(Component name, List<Enchantment> elements, List<EnchantmentGroup> groups, LevelRange levelRange, Style targetStyle) {
        this.name = name;
        this.elements.addAll(elements);
        this.groups.addAll(groups);
        this.levelRange = levelRange;
        this.targetStyle = targetStyle;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static EnchantmentColor fromCodec(Component name, List<Enchantment> elements, List<EnchantmentGroup> groups, Optional<LevelRange> levelRange, Style targetStyle) {
        return new EnchantmentColor(name, elements, groups, levelRange.orElse(null), targetStyle);
    }

    public Component getName() {
        return name;
    }

    public List<Enchantment> getElements() {
        return elements;
    }

    public Style getTargetStyle() {
        return targetStyle;
    }

    public List<EnchantmentGroup> getGroups() {
        return groups;
    }

    public void setName(Component name) {
        this.name = name;
    }

    public void setLevelRange(@Nullable LevelRange levelRange) {
        this.levelRange = levelRange;
    }

    public void setTargetStyle(Style targetStyle) {
        this.targetStyle = targetStyle;
    }

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
