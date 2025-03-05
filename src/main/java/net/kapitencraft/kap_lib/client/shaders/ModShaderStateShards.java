package net.kapitencraft.kap_lib.client.shaders;

import net.minecraft.client.renderer.RenderStateShard;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ModShaderStateShards {
    RenderStateShard.ShaderStateShard CHROMATIC = new RenderStateShard.ShaderStateShard(ModShaders::getRendertypeChromaShader);
    RenderStateShard.ShaderStateShard GUI_CHROMA = new RenderStateShard.ShaderStateShard(ModShaders::getGuiChromaShader);
}