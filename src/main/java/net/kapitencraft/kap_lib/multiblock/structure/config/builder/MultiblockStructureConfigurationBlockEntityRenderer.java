package net.kapitencraft.kap_lib.multiblock.structure.config.builder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockStructureConfigurationBlockEntityRenderer implements BlockEntityRenderer<MultiblockStructureConfigurationBlockEntity> {
    public MultiblockStructureConfigurationBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MultiblockStructureConfigurationBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (Minecraft.getInstance().player.canUseGameMasterBlocks() || Minecraft.getInstance().player.isSpectator()) {
            BlockPos blockpos = blockEntity.getStructurePos();
            Vec3i vec3i = blockEntity.getStructureSize();
            if (vec3i.getX() >= 1 && vec3i.getY() >= 1 && vec3i.getZ() >= 1) {
                double d0 = blockpos.getX();
                double d1 = blockpos.getZ();
                double d5 = blockpos.getY();
                double d8 = d5 + (double) vec3i.getY();
                double d2 = vec3i.getX();
                double d3 = vec3i.getZ();

                double d4 = d2 < 0.0 ? d0 + 1.0 : d0;
                double d6 = d3 < 0.0 ? d1 + 1.0 : d1;
                double d7 = d4 + d2;
                double d9 = d6 + d3;

                VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
                LevelRenderer.renderLineBox(poseStack, vertexconsumer, d4, d5, d6, d7, d8, d9, 0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
                this.renderInvisibleBlocks(blockEntity, bufferSource, poseStack);
            }
        }
    }

    private void renderInvisibleBlocks(MultiblockStructureConfigurationBlockEntity blockEntity, MultiBufferSource bufferSource, PoseStack poseStack) {
        BlockGetter blockgetter = blockEntity.getLevel();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
        BlockPos blockEntityPos = blockEntity.getBlockPos();
        BlockPos blockpos1 = blockEntityPos.offset(1, 1, 1);

        for (BlockPos blockpos2 : BlockPos.betweenClosed(blockpos1, blockpos1.offset(blockEntity.getStructureSize()).offset(-1, -1, -1))) {
            BlockState blockstate = blockgetter.getBlockState(blockpos2);

            BlockPos relative = blockpos2.subtract(blockEntityPos);
            if (true) {
                DebugRenderer.renderFloatingText(poseStack, bufferSource, "test", blockpos2.getX() + .5, blockpos2.getY(), blockpos2.getZ() + .5, -1);
            }
        }
    }

    public boolean shouldRenderOffScreen(MultiblockStructureConfigurationBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(MultiblockStructureConfigurationBlockEntity blockEntity) {
        return net.minecraft.world.phys.AABB.INFINITE;
    }
}
