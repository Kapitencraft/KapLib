package net.kapitencraft.kap_lib.core.mixin.classes.client;

import net.kapitencraft.kap_lib.core.event.custom.client.FontSetsEvent;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(FontManager.class)
public class FontManagerMixin {

    @Shadow
    @Final
    private Map<ResourceLocation, FontSet> fontSets;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addPlayerHeadFontSet(TextureManager pTextureManager, CallbackInfo ci) {
        fireAddFontsetsEvent(new FontSetsEvent.Register(this.fontSets, pTextureManager));
    }

    @Inject(method = "apply", at = @At("TAIL"))
    private void addPlayerHeadFontSetApply(FontManager.Preparation pPreperation, ProfilerFiller pProfiler, CallbackInfo ci) {
        fireAddFontsetsEvent(new FontSetsEvent.Update(this.fontSets));
    }

    @Unique
    private void fireAddFontsetsEvent(FontSetsEvent event) {
        ModLoader.postEvent(event);
    }
}
