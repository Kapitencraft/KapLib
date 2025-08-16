package net.kapitencraft.kap_lib.client.shaders;

import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

@OnlyIn(Dist.CLIENT)
@ApiStatus.Internal
public class ShaderHelper {

    public static void updateUniforms(ShaderInstance instance) {
        UniformsProvider.applyVectors(instance);
        UniformsProvider.applySingletons(instance);
    }
}
