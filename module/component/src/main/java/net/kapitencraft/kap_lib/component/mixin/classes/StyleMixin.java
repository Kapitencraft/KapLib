package net.kapitencraft.kap_lib.component.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.component.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffects;
import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(Style.class)
public abstract class StyleMixin implements EffectsStyle, MixinSelfProvider<Style> {
    @Shadow
    @Nullable
    public TextColor color;

    @Shadow
    @Final
    @Nullable
    Boolean bold;

    @Shadow
    @Final
    @Nullable
    Boolean italic;

    @Shadow
    @Final
    @Nullable
    Boolean underlined;

    @Shadow
    @Final
    @Nullable
    Boolean strikethrough;

    @Shadow
    @Final
    @Nullable
    HoverEvent hoverEvent;

    @Shadow
    @Final
    @Nullable
    Boolean obfuscated;

    @Shadow
    @Final
    @Nullable
    ClickEvent clickEvent;

    @Shadow
    @Final
    @Nullable
    String insertion;

    @Shadow
    @Final
    @Nullable
    ResourceLocation font;

    @SuppressWarnings("all")
    @NotNull
    private GlyphEffect.EffectInstance[] effects = new GlyphEffect.EffectInstance[0];

    @SuppressWarnings("all")
    public Style addEffect(GlyphEffect.EffectInstance effect) {
        Style style = new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
        GlyphEffect.EffectInstance[] s = new GlyphEffect.EffectInstance[this.effects.length + 1];
        System.arraycopy(this.effects, 0, s, 0, this.effects.length);
        s[this.effects.length] = effect;
        style.setEffects(s);
        return style;
    }

    @Override
    public Style removeEffect(GlyphEffect<?> effect) {
        int idx = -1;
        for (int i = 0; i < this.effects.length; i++) {
            if (this.effects[i].getEffect() == effect) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return self();
        Style style = new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
        if (this.effects.length - 1 > 0) {
            GlyphEffect.EffectInstance[] s = new GlyphEffect.EffectInstance[this.effects.length - 1];
            System.arraycopy(this.effects, 0, s, 0, idx);
            if (this.effects.length - idx > 1)
                System.arraycopy(this.effects, idx + 1, s, idx + 1, this.effects.length - idx - 1);
            style.setEffects(s);
        }
        return style;
    }

    @Override
    public void setEffects(GlyphEffect.EffectInstance[] effects) {
        this.effects = effects;
    }

    @Unique
    public GlyphEffect.EffectInstance @NotNull [] getEffects() {
        return effects;
    }

    @WrapOperation(method = "toString", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;"))
    public StringBuilder toString(StringBuilder instance, String str, Operation<StringBuilder> original) {
        original.call(instance, str);
        if (this.effects.length > 0)
            instance.append("{special: ").append(Arrays.stream(this.effects).map(GlyphEffect.EffectInstance::getEffect).map(GlyphEffects::getKey).filter(Objects::nonNull).map(ResourceLocation::toString).collect(Collectors.joining(", "))).append("}");
        return instance;
    }

    @WrapOperation(method = {
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
    private Style makeNewStyle(TextColor color, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, ClickEvent clickEvent, HoverEvent hoverEvent, String insertion, ResourceLocation font, Operation<Style> original) {
        Style style = original.call(color, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font);
        style.setEffects(this.effects);
        return style;
    }
}