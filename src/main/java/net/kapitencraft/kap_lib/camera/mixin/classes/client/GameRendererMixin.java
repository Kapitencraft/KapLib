package net.kapitencraft.kap_lib.camera.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.camera.core.CameraController;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"))
    private void addCameraControl(Camera instance, BlockGetter level, Entity entity, boolean detached, boolean pThirdPersonReverse, float pPartialTicks, Operation<Void> original) {
        CameraController controller = CameraController.INSTANCE;
        if (controller.running()) {
            if (controller.data.target == null || controller.data.detached) {
                detached = true;
                pThirdPersonReverse = !controller.data.thirdPerson;
            }
        }

        original.call(instance, level, entity, detached, pThirdPersonReverse, pPartialTicks);
    }

    @Redirect(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
    private boolean disableHandRendering(CameraType instance) {
        return instance.isFirstPerson() && !CameraController.INSTANCE.running();
    }
}
