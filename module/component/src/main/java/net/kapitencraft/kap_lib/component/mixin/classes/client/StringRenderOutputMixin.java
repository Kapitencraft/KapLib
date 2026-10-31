package net.kapitencraft.kap_lib.component.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kapitencraft.kap_lib.component.font.effect.EffectSettings;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Font.StringRenderOutput.class)
public abstract class StringRenderOutputMixin {
    @Shadow
    @Final
    private boolean dropShadow;

    @WrapOperation(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;renderType(Lnet/minecraft/client/gui/Font$DisplayMode;)Lnet/minecraft/client/renderer/RenderType;"))
    public RenderType changeRender(BakedGlyph instance, Font.DisplayMode displayMode, Operation<RenderType> original, @Local(argsOnly = true) Style style) {
        if (style.hasEffect(GlyphEffects.RAINBOW) && !this.dropShadow) {
            return instance.getChromaType();
        }
        return original.call(instance, displayMode);
    }

    @WrapOperation(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;renderChar(Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;ZZFFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"))
    private void injectEffects(Font instance, BakedGlyph glyph, boolean bold, boolean italic,
                               float boldOffset, float x, float y, Matrix4f matrix,
                               VertexConsumer buffer, float red, float green, float blue,
                               float alpha, int packedLight, Operation<Void> original,
                               @Local(argsOnly = true) Style style,
                               @Local(argsOnly = true, ordinal = 0) int index
    ) {
        if (style.hasEffects()) {
            EffectSettings settings = new EffectSettings();
            settings.r = red;
            settings.g = green;
            settings.b = blue;
            settings.x = x;
            settings.y = y;
            settings.isShadow = dropShadow;
            settings.index = index;

            for (GlyphEffect.EffectInstance effectInstance : style.getEffects())
                effectInstance.apply(settings);

            x = settings.x;
            y = settings.y;
            red = settings.r;
            green = settings.g;
            blue = settings.b;
        }
        original.call(instance, glyph, bold, italic, boldOffset, x, y, matrix, buffer, red, green, blue, alpha, packedLight);
    }
}
