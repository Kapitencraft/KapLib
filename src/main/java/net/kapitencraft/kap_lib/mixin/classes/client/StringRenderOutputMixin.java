package net.kapitencraft.kap_lib.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.component.font.effect.EffectSettings;
import net.kapitencraft.kap_lib.component.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.component.registry.GlyphEffects;
import net.kapitencraft.kap_lib.component.mixin.duck.IChromatic;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.StringRenderOutput.class)
public abstract class StringRenderOutputMixin {

    @Shadow
    float x;

    @Shadow
    float y;

    @Shadow @Final private boolean dropShadow;

    @Shadow
    private float r;

    @Shadow
    private float g;

    @Shadow
    private float b;

    @WrapOperation(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;renderType(Lnet/minecraft/client/gui/Font$DisplayMode;)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType changeRender(BakedGlyph instance, Font.DisplayMode displayMode, Operation<RenderType> original, @Local(argsOnly = true) Style style) {
        if (EffectsStyle.of(style).hasEffect(GlyphEffects.RAINBOW.get()) && !this.dropShadow) {
            return ((IChromatic) instance).getChromaType();
        }
        return instance.renderType(displayMode);
    }

    @Inject(method = "accept", at = @At("HEAD"))
    public void accept(int index, Style style, int i, CallbackInfoReturnable<Boolean> returnable) {
        EffectsStyle effects = (EffectsStyle) style;
        if (effects.hasEffects()) {
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

            for (GlyphEffect effect : effects.getEffects()) effect.apply(settings);
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
