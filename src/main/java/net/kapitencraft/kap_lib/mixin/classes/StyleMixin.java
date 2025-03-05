package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.client.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.*;
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
    @NotNull
    private GlyphEffect[] effects = new GlyphEffect[0];

    @SuppressWarnings("all")
    public Style addEffect(GlyphEffect effect) {
        Style style = new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
        GlyphEffect[] s = new GlyphEffect[this.effects.length + 1];
        System.arraycopy(this.effects, 0, s, 0, this.effects.length);
        s[this.effects.length] = effect;
        EffectsStyle.of(style).setEffects(s);
        return style;
    }

    @Override
    public void setEffects(GlyphEffect[] effects) {
        this.effects = effects;
    }

    @Unique
    public GlyphEffect @NotNull [] getEffects() {
        return effects;
    }

    @Redirect(method = "toString", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;"))
    public StringBuilder toString(StringBuilder instance, String str) {
        instance.append(str);
        if (this.effects.length > 0) instance.append("{special: ").append(Arrays.stream(this.effects).map(ExtraRegistries.GLYPH_EFFECTS::getKey).filter(Objects::nonNull).map(ResourceLocation::toString).collect(Collectors.joining(", "))).append("}");
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
        EffectsStyle.of(style).setEffects(this.effects);
        return style;
    }
}