package net.kapitencraft.kap_lib.mixin.classes.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.Uniform;
import net.kapitencraft.kap_lib.client.shaders.BlockRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow protected abstract void renderSectionLayer(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix);

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void renderChunkLayerExtensions(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local(ordinal = 0) double d0, @Local(ordinal = 1) double d1, @Local(ordinal = 2) double d2) {
        for (RenderType renderType : BlockRenderTypes.RENDER_TYPES) {
            renderSectionLayer(renderType, d0, d1, d2, frustumMatrix, projectionMatrix);
        }
    }

    @Inject(method = "renderSectionLayer", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;bind()V"))
    private void addChunkPositionUniform(
            RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, CallbackInfo ci,
            //locals
            @Local ShaderInstance shaderInstance, @Local BlockPos pos
    ) {
        Uniform chunkPosition = shaderInstance.getUniform("ChunkPosition");
        if (chunkPosition != null) {
            chunkPosition.set(pos.getX(), pos.getY(), pos.getZ());
            chunkPosition.upload();
        }
    }
}