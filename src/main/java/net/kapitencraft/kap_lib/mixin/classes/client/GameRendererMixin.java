package net.kapitencraft.kap_lib.mixin.classes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.cam.core.CameraController;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow protected abstract void renderItemInHand(PoseStack pPoseStack, Camera pActiveRenderInfo, float pPartialTicks);

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"))
    private void addCameraControl(Camera instance, BlockGetter pLevel, Entity pEntity, boolean pDetached, boolean pThirdPersonReverse, float pPartialTicks) {
        CameraController controller = LibClient.cameraControl;
        if (controller.running()) {
            if (controller.data.target == null || controller.data.detached) {
                pDetached = true;
                pThirdPersonReverse = !controller.data.thirdPerson;
            }
        }

        instance.setup(pLevel, pEntity, pDetached, pThirdPersonReverse, pPartialTicks);
    }

    @Redirect(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
    private boolean disableHandRendering(CameraType instance) {
        return instance.isFirstPerson() && !LibClient.cameraControl.running();
    }
}
