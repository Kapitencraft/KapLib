package net.kapitencraft.kap_lib.mixin.classes.client;

import com.mojang.blaze3d.font.SheetGlyphInfo;
import net.kapitencraft.kap_lib.client.shaders.ModRenderTypes;
import net.kapitencraft.kap_lib.mixin.duck.IChromatic;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FontSet.class)
public class FontSetMixin {

    @Inject(method = "stitch", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addChromaToTexture(SheetGlyphInfo info, CallbackInfoReturnable<BakedGlyph> cir, ResourceLocation location, boolean $$4, GlyphRenderTypes $$5, FontTexture texture) {
        IChromatic.of(texture).setChromaType(ModRenderTypes.chromatic(location));
    }

    @Redirect(method = "stitch", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/FontTexture;add(Lcom/mojang/blaze3d/font/SheetGlyphInfo;)Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;"))
    private BakedGlyph addChromaToGlyph(FontTexture instance, SheetGlyphInfo pGlyphInfo) {
        BakedGlyph glyph = instance.add(pGlyphInfo);
        if (glyph != null) IChromatic.of(glyph).setChromaType(IChromatic.of(instance).getChromaType());
        return glyph;
    }
}
