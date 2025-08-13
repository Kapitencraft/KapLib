package net.kapitencraft.kap_lib.mixin.classes.client;

import net.kapitencraft.kap_lib.client.glyph.enchantment_applicable.EnchantmentApplicableAllocator;
import net.kapitencraft.kap_lib.client.glyph.player_head.PlayerHeadAllocator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(FontManager.class)
public class FontManagerMixin {

    @Shadow @Final private Map<ResourceLocation, FontSet> fontSets;

    @Shadow @Final private TextureManager textureManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addPlayerHeadFontSet(TextureManager pTextureManager, CallbackInfo ci) {
        this.fontSets.put(PlayerHeadAllocator.FONT, new PlayerHeadAllocator(Minecraft.getInstance().getSkinManager(), pTextureManager));
        this.fontSets.put(EnchantmentApplicableAllocator.FONT, new EnchantmentApplicableAllocator(pTextureManager));
    }

    @Inject(method = "apply", at = @At("TAIL"))
    private void addPlayerHeadFontSetApply(FontManager.Preparation pPreperation, ProfilerFiller pProfiler, CallbackInfo ci) {
        this.fontSets.put(PlayerHeadAllocator.FONT, PlayerHeadAllocator.getInstance());
        this.fontSets.put(EnchantmentApplicableAllocator.FONT, EnchantmentApplicableAllocator.getInstance());
    }
}
