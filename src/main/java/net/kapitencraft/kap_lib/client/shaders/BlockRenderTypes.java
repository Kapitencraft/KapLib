package net.kapitencraft.kap_lib.client.shaders;

import net.kapitencraft.kap_lib.event.custom.client.RegisterChunkBufferLayersEvent;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class BlockRenderTypes {
    public static final List<RenderType> RENDER_TYPES = new ArrayList<>();

    public static void register() {
        ModLoader.postEvent(new RegisterChunkBufferLayersEvent());
    }
}