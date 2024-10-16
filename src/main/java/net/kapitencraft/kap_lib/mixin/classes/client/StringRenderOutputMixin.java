package net.kapitencraft.kap_lib.mixin.classes.client;

import net.kapitencraft.kap_lib.client.font.effect.EffectSettings;
import net.kapitencraft.kap_lib.client.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.registry.ModGlyphEffects;
import net.kapitencraft.kap_lib.mixin.duck.IChromatic;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.StringRenderOutput.class)
public abstract class StringRenderOutputMixin {

    @Shadow
    float x;

    @Shadow
    float y;

    @Shadow @Final private boolean dropShadow;

    @Shadow public float r;

    @Shadow public float g;

    @Shadow public float b;

    @Redirect(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;renderType(Lnet/minecraft/client/gui/Font$DisplayMode;)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType changeRender(BakedGlyph instance, Font.DisplayMode pDisplayMode, int pIndex, Style pStyle, int pId) {
        if (EffectsStyle.of(pStyle).getEffects().contains(ModGlyphEffects.RAINBOW.get()) && !this.dropShadow) {
            return ((IChromatic) instance).getChromaType();
        }
        return instance.renderType(pDisplayMode);
    }

    @Inject(method = "accept", at = @At("HEAD"))
    public void accept(int index, Style style, int i, CallbackInfoReturnable<Boolean> returnable) {
        EffectsStyle effects = (EffectsStyle) style;
        if (effects.getEffects() != null && !effects.getEffects().isEmpty()) {
            EffectSettings settings = new EffectSettings();
            float r,g,b;
            r = this.r;
            g = this.g;
            b = this.b;
            settings.r = this.r;
            settings.g = this.g;
            settings.b = this.b;
            settings.x = this.x;
            settings.y = this.y;
            settings.isShadow = this.dropShadow;
            settings.index = index;

            effects.getEffects().forEach(glyphEffect -> glyphEffect.apply(settings));
            this.x = settings.x;
            this.y = settings.y;
            if (r != settings.r || g != settings.g || b != settings.b) {
                this.r = settings.r;
                this.g = settings.g;
                this.b = settings.b;
                style.color = null;
            }
        }
    }
}
