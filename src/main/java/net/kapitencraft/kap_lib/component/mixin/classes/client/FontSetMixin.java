package net.kapitencraft.kap_lib.component.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import net.kapitencraft.kap_lib.component.mixin.duck.IChromatic;
import net.kapitencraft.kap_lib.shader.ModRenderTypes;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontSet.class)
public class FontSetMixin {

    @Inject(method = "stitch", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void addChromaToTexture(SheetGlyphInfo info, CallbackInfoReturnable<BakedGlyph> cir, @Local ResourceLocation location, @Local FontTexture texture) {
        IChromatic.of(texture).setChromaType(ModRenderTypes.chromatic(location));
    }

    @WrapOperation(method = "stitch", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/FontTexture;add(Lcom/mojang/blaze3d/font/SheetGlyphInfo;)Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;"))
    private BakedGlyph addChromaToGlyph(FontTexture instance, SheetGlyphInfo glyphInfo, Operation<BakedGlyph> original) {
        BakedGlyph glyph = original.call(instance, glyphInfo);
        if (glyph != null) IChromatic.of(glyph).setChromaType(IChromatic.of(instance).getChromaType());
        return glyph;
    }
}
