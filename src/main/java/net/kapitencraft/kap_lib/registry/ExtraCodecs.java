package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.client.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExtraCodecs {

    /**
     * style mixin serializing custom glyph effects
     * <br> due to style being loaded before registries are, it can't be implemented into the style codec itself
     */
    Codec<Style> EFFECT_SERIALIZING_STYLE = RecordCodecBuilder.create(styleInstance -> styleInstance.group(
            TextColor.CODEC.optionalFieldOf("color").forGetter((style) -> Optional.ofNullable(style.color)),
            Codec.BOOL.optionalFieldOf("bold", false).forGetter(Style::isBold),
            Codec.BOOL.optionalFieldOf("italic", false).forGetter(Style::isItalic),
            Codec.BOOL.optionalFieldOf("underlined", false).forGetter(Style::isUnderlined),
            Codec.BOOL.optionalFieldOf("strikethrough", false).forGetter(Style::isStrikethrough),
            Codec.BOOL.optionalFieldOf("obfuscated", false).forGetter(Style::isObfuscated),
            Codec.STRING.optionalFieldOf("insertion").forGetter((p_237269_) -> Optional.ofNullable(p_237269_.getInsertion())),
            ResourceLocation.CODEC.optionalFieldOf("font", Style.DEFAULT_FONT).forGetter(Style::getFont),
            ExtraRegistries.GLYPH_EFFECTS.byNameCodec().listOf().optionalFieldOf("effects", List.of()).forGetter(style -> List.of(EffectsStyle.of(style).getEffects()))
    ).apply(styleInstance, ExtraCodecs::createStyleFromCodec));

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static Style createStyleFromCodec(Optional<TextColor> color, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, Optional<String> insertion, ResourceLocation font, List<GlyphEffect> effects) {
        Style style = new Style(color.orElse(null), bold, italic, underlined, strikethrough, obfuscated, null, null, insertion.orElse(null), font);
        EffectsStyle.of(style).setEffects(effects.toArray(GlyphEffect[]::new));
        return style;
    }

    /**
     * @deprecated use {@link UUIDUtil#STRING_CODEC} instead
     */
    @Deprecated(forRemoval = true, since = "1.26.4")
    Codec<UUID> UUID = UUIDUtil.STRING_CODEC;
}
