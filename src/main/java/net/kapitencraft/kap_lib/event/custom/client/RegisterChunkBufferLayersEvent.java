package net.kapitencraft.kap_lib.event.custom.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.kapitencraft.kap_lib.client.shaders.BlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

/**
 * register chunk buffer layers able to be used for block rendering
 */
public class RegisterChunkBufferLayersEvent extends Event implements IModBusEvent {

    /**
     * @param renderType the rendertype to register. must use format block
     */
    public void register(RenderType renderType) {
        if (renderType.format() != DefaultVertexFormat.BLOCK) throw new IllegalStateException("chunk layer buffers must be of format block!");
        BlockRenderTypes.RENDER_TYPES.add(renderType);
    }
}
