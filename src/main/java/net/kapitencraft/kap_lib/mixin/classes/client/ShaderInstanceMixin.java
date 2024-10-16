package net.kapitencraft.kap_lib.mixin.classes.client;

import net.kapitencraft.kap_lib.client.shaders.ShaderHelper;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderInstance.class)
public class ShaderInstanceMixin {
    private ShaderInstance self() {
        return (ShaderInstance) (Object) this;
    }

    @Inject(method = "apply", at = @At("HEAD"))
    private void applyUniforms(CallbackInfo ci) {
        ShaderHelper.updateUniforms(self());
    }
}
