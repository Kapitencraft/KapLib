package net.kapitencraft.kap_lib.two_handed.mixin.classes.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private static ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE;

    @Shadow @Final private static ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE;

    @Shadow @Final private static ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE;

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    private void addOffhandAttackCooldownBar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        float offhandCooldown = ((Player) this.minecraft.player).getOffhandAttackStrengthScale(0);
        boolean flag = false;
        if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && offhandCooldown >= 1.0F) {
            flag = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
            flag &= this.minecraft.crosshairPickEntity.isAlive();
        }

        int j = guiGraphics.guiHeight() / 2 - 7 + 26;
        int k = guiGraphics.guiWidth() / 2 - 8;
        if (flag) {
            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, k, j, 16, 16);
        } else if (offhandCooldown < 1.0F) {
            int l = (int)(offhandCooldown * 17.0F);
            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k, j, 16, 4);
            guiGraphics.blitSprite(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, k, j, l, 4);
        }
    }
}
