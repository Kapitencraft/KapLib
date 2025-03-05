package net.kapitencraft.kap_lib.client.shaders;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
        event.registerShader(new ShaderInstance(event.getResourceProvider(), KapLibMod.res("rendertype_chroma"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), shaderInstance -> rendertypeChromaShader = shaderInstance);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), KapLibMod.res("gui_chroma"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), inst -> guiChromaShader = inst);
    }
}