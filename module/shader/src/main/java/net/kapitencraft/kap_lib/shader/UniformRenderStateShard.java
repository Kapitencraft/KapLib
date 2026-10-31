package net.kapitencraft.kap_lib.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.Map;

public class UniformRenderStateShard extends RenderStateShard {
    public UniformRenderStateShard(Map<String, float[]> params) {
        super("uniforms", () -> {
            ShaderInstance shader = RenderSystem.getShader();
            if (shader != null)
                params.forEach((s, value) -> {
                            Uniform uniform = shader.getUniform(s);
                            if (uniform == null)
                                LibConstants.LOGGER.warn("no uniform '{}' in shader {}", s, shader.getName());
                            else
                                uniform.set(value);
                        }
                );
        }, () -> {
        });
    }
}
