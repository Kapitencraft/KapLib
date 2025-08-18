package net.kapitencraft.kap_lib.client.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.Nullable;

public class SimpleParticleRenderType implements ParticleRenderType {
    private final RenderType type;

    public SimpleParticleRenderType(RenderType type) {
        this.type = type;
    }

    @Override
    public @Nullable BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
        type.setupRenderState();
        return tesselator.begin(type.mode, type.format);
    }
}
