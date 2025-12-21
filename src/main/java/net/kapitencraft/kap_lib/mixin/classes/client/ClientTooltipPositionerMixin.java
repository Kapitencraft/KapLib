package net.kapitencraft.kap_lib.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

    @WrapOperation(method = "positionTooltip(IIIIII)Lorg/joml/Vector2ic;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/DefaultTooltipPositioner;positionTooltip(IILorg/joml/Vector2i;II)V"))
    public void clearYSnap(DefaultTooltipPositioner instance, int screenWidth, int screenHeight, Vector2i tooltipPos, int tooltipWidth, int tooltipHeight, Operation<Void> original) {
        if (tooltipPos.x + tooltipWidth > screenWidth) {
            tooltipPos.x = Math.max(tooltipPos.x - 24 - tooltipWidth, 4);
        }
    }
}