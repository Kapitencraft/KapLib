package net.kapitencraft.kap_lib.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.kapitencraft.kap_lib.shader.chroma.ChromaOrigin;
import net.kapitencraft.kap_lib.shader.chroma.ChromaType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * custom render types
 */
public class ShaderRenderTypes extends RenderType {
    private ShaderRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    /**
     * @param locationIn the texture that should be applied to this rendertype
     * @return the chromatic rendertype for the given texture
     */
    public static RenderType chromatic(ResourceLocation locationIn, float speed, ChromaType chromaType, float spacing, ChromaOrigin origin) {
        CompositeState state = CompositeState.builder()
                .setShaderState(ModShaderStateShards.CHROMATIC)
                .setTextureState(new TextureStateShard(locationIn, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setUniformsStateShard(new UniformRenderStateShard(Map.of("ChromaConfig", new float[] {origin.getConfigId(), spacing, speed, chromaType.getConfigId()})))
                .createCompositeState(false);
        return create("chromatic", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, state);
    }

    public static final RenderType FILL_CHROMA = create("chroma", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true,
            CompositeState.builder()
                    .setShaderState(ModShaderStateShards.GUI_CHROMA)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false)
    );
}
