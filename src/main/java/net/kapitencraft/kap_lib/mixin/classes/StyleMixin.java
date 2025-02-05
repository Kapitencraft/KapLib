package net.kapitencraft.kap_lib.mixin.classes;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.client.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mixin(Style.class)
public abstract class StyleMixin implements EffectsStyle {
    @Shadow @Nullable public TextColor color;

    @Shadow @Final @Nullable
    Boolean bold;

    @Shadow @Final @Nullable
    Boolean italic;

    @Shadow @Final @Nullable
    Boolean underlined;

    @Shadow @Final @Nullable
    Boolean strikethrough;

    @Shadow @Final @Nullable
    HoverEvent hoverEvent;

    @Shadow @Final @Nullable
    Boolean obfuscated;

    @Shadow @Final @Nullable
    ClickEvent clickEvent;

    @Shadow @Final @Nullable
    String insertion;

    @Shadow @Final @Nullable
    ResourceLocation font;

    @Shadow @Final public static ResourceLocation DEFAULT_FONT;

    private Style self() {
        return (Style) (Object) this;
    }

    @SuppressWarnings("all")
    private final List<GlyphEffect> effects = new ArrayList<>();

    @SuppressWarnings("all")
    public Style addEffect(GlyphEffect effect) {
        Style style = new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
        List<GlyphEffect> s = EffectsStyle.of(style).getEffects();
        s.addAll(this.effects);
        s.add(effect);
        return style;
    }

    @Unique
    public List<GlyphEffect> getEffects() {
        return effects;
    }

    @Redirect(method = "toString", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;"))
    public StringBuilder toString(StringBuilder instance, String str) {
        instance.append(str);
        if (!this.effects.isEmpty()) instance.append("{special: ").append(this.effects.stream().map(ExtraRegistries.GLYPH_EFFECTS::getKey).filter(Objects::nonNull).map(ResourceLocation::toString).collect(Collectors.joining(", "))).append("}");
        return instance;
    }

    @Redirect(method = {
            "withBold",
            "withClickEvent",
            "withFont",
            "withInsertion",
            "withItalic",
            "withColor(Lnet/minecraft/network/chat/TextColor;)Lnet/minecraft/network/chat/Style;",
            "withHoverEvent",
            "withObfuscated",
            "withStrikethrough",
            "withUnderlined",
            "applyTo"
    }, at = @At(value = "NEW", target = "(Lnet/minecraft/network/chat/TextColor;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Lnet/minecraft/network/chat/ClickEvent;Lnet/minecraft/network/chat/HoverEvent;Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/network/chat/Style;"))
    private Style makeNewStyle(TextColor pColor, Boolean pBold, Boolean pItalic, Boolean pUnderlined, Boolean pStrikethrough, Boolean pObfuscated, ClickEvent pClickEvent, HoverEvent pHoverEvent, String pInsertion, ResourceLocation pFont) {
        Style style = new Style(pColor, pBold, pItalic, pUnderlined, pStrikethrough, pObfuscated, pClickEvent, pHoverEvent, pInsertion, pFont);
        EffectsStyle.of(style).getEffects().addAll(EffectsStyle.of(self()).getEffects());
        return style;
    }

    //TODO find a way to use registries :prayge:
    //@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    //private static Codec<Style> addEffectsToCodec(Function<RecordCodecBuilder.Instance<Style>, ? extends App<RecordCodecBuilder.Mu<Style>, Style>> ignored) {
    //    return RecordCodecBuilder.create(styleInstance -> styleInstance.group(
    //            TextColor.CODEC.optionalFieldOf("color").forGetter((style) -> Optional.ofNullable(style.color)),
    //            Codec.BOOL.optionalFieldOf("bold", false).forGetter(Style::isBold),
    //            Codec.BOOL.optionalFieldOf("italic", false).forGetter(Style::isItalic),
    //            Codec.BOOL.optionalFieldOf("underlined", false).forGetter(Style::isUnderlined),
    //            Codec.BOOL.optionalFieldOf("strikethrough", false).forGetter(Style::isStrikethrough),
    //            Codec.BOOL.optionalFieldOf("obfuscated", false).forGetter(Style::isObfuscated),
    //            Codec.STRING.optionalFieldOf("insertion").forGetter((p_237269_) -> Optional.ofNullable(p_237269_.getInsertion())),
    //            ResourceLocation.CODEC.optionalFieldOf("font", DEFAULT_FONT).forGetter(Style::getFont),
    //            ExtraRegistries.GLYPH_EFFECTS.getCodec().listOf().fieldOf("effects").forGetter(style -> EffectsStyle.of(style).getEffects())
    //    ).apply(styleInstance, StyleMixin::createFromCodec));
    //}

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Unique
    private static Style createFromCodec(Optional<TextColor> color, boolean bold, boolean italic, boolean underlined, boolean strikethrough, boolean obfuscated, Optional<String> insertion, ResourceLocation font, List<GlyphEffect> effects) {
        Style style = new Style(color.orElse(null), bold, italic, underlined, strikethrough, obfuscated, null, null, insertion.orElse(null), font);
        EffectsStyle.of(style).getEffects().addAll(effects);
        return style;
    }
}