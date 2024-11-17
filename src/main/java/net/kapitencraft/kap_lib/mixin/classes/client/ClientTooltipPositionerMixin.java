package net.kapitencraft.kap_lib.mixin.classes.client;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DefaultTooltipPositioner.class)
public abstract class ClientTooltipPositionerMixin implements ClientTooltipPositioner {

    @Redirect(method = "positionTooltip(IIIIII)Lorg/joml/Vector2ic;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/DefaultTooltipPositioner;positionTooltip(IILorg/joml/Vector2i;II)V"))
    public void clearYSnap(DefaultTooltipPositioner instance, int pScreenWidth, int pScreenHeight, Vector2i pTooltipPos, int pTooltipWidth, int pTooltipHeight) {
        if (pTooltipPos.x + pTooltipWidth > pScreenWidth) {
            pTooltipPos.x = Math.max(pTooltipPos.x - 24 - pTooltipWidth, 4);
        }
    }
}