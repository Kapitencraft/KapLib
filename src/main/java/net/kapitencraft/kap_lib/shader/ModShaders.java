package net.kapitencraft.kap_lib.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

@EventBusSubscriber(value = Dist.CLIENT)
public class ModShaders {

    @Nullable
    private static ShaderInstance rendertypeChromaShader, guiChromaShader;

    public static ShaderInstance getRendertypeChromaShader() {
        return Objects.requireNonNull(rendertypeChromaShader, "attempted to get Rendertype Chroma before load");
    }

    public static ShaderInstance getGuiChromaShader() {
        return Objects.requireNonNull(guiChromaShader, "attempted to get Chroma shader before load");
    }

    @SubscribeEvent
    public static void createShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), LibConstants.res("rendertype_chroma"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), shaderInstance -> rendertypeChromaShader = shaderInstance);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), LibConstants.res("gui_chroma"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), inst -> guiChromaShader = inst);
    }
}