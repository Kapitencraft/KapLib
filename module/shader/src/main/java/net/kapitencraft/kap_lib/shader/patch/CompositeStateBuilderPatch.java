package net.kapitencraft.kap_lib.shader.patch;

import net.kapitencraft.kap_lib.shader.UniformRenderStateShard;
import net.minecraft.client.renderer.RenderType;

public interface CompositeStateBuilderPatch {

    default RenderType.CompositeState.CompositeStateBuilder setUniformsStateShard(UniformRenderStateShard stateShard) {
        throw new AssertionError();
    }
}
