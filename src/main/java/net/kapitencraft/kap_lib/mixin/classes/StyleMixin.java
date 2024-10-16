package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.client.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.registry.custom.core.ModRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(Style.class)
public abstract class StyleMixin implements EffectsStyle {
    private Style self() {
        return (Style) (Object) this;
    }

    @SuppressWarnings("all")
    private final List<GlyphEffect> effects = new ArrayList<>();

    @SuppressWarnings("all")
    public void addEffect(GlyphEffect effect) {
        effects.add(effect);
    }

    @Unique
    public List<GlyphEffect> getEffects() {
        return effects;
    }

    @Redirect(method = "toString", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;"))
    public StringBuilder toString(StringBuilder instance, String str) {
        instance.append(str);
        if (!this.effects.isEmpty()) instance.append("{special: ").append(this.effects.stream().map(ModRegistries.GLYPH_EFFECTS::getKey).filter(Objects::nonNull).map(ResourceLocation::toString).collect(Collectors.joining(", "))).append("}");
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
}