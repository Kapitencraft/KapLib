package net.kapitencraft.kap_lib.camera.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.camera.core.CameraController;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class CameraMixin {

    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    private void overrideSetupWhenControllerActive(Camera instance, double x, double y, double z, Operation<Void> original, @Local(name = "partialTick") float partialTick) {
        Vec3 position = CameraController.INSTANCE.getCamPosition(partialTick, new Vec3(x, y, z));
        original.call(instance, position.x, position.y, position.z);
    }
}