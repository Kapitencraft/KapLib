package net.kapitencraft.kap_lib.mixin.classes.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.kapitencraft.kap_lib.client.shaders.BlockRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {


    @Shadow protected abstract void renderChunkLayer(RenderType pRenderType, PoseStack pPoseStack, double pCamX, double pCamY, double pCamZ, Matrix4f pProjectionMatrix);

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderChunkLayerExtensions(
            PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, CallbackInfo ci,
            //locals
            ProfilerFiller profilerFiller, Vec3 vec3, double d0, double d1, double d2
    ) {
        for (RenderType renderType : BlockRenderTypes.RENDER_TYPES) {
            renderChunkLayer(renderType, pPoseStack, d0, d1, d2, pProjectionMatrix);
        }
    }

    @Inject(method = "renderChunkLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk;getOrigin()Lnet/minecraft/core/BlockPos;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addChunkPositionUniform(
            RenderType pRenderType, PoseStack pPoseStack, double pCamX, double pCamY, double pCamZ, Matrix4f pProjectionMatrix,
            //locals
            CallbackInfo ci, boolean flag1, ObjectListIterator objectlistiterator, ShaderInstance shaderinstance, Uniform uniform, LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo1, ChunkRenderDispatcher.RenderChunk chunk, VertexBuffer vertexbuffer) {
        BlockPos pos = chunk.getOrigin();
        Uniform chunkPosition = shaderinstance.getUniform("ChunkPosition");
        if (chunkPosition != null) {
            chunkPosition.set(pos.getX(), pos.getY(), pos.getZ());
        }
    }
}