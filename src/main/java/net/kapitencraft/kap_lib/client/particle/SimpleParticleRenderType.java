package net.kapitencraft.kap_lib.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;

public class SimpleParticleRenderType implements ParticleRenderType {
    private final RenderType type;

    public SimpleParticleRenderType(RenderType type) {
        this.type = type;
    }

    @Override
    public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
        pBuilder.begin(type.mode(), type.format());
    }

    @Override
    public void end(Tesselator pTesselator) {
        type.end(pTesselator.getBuilder(), RenderSystem.getVertexSorting());
    }
}
