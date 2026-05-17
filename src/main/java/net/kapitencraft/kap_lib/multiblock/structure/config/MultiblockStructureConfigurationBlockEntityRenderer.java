package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockStructureConfigurationBlockEntityRenderer implements BlockEntityRenderer<MultiblockStructureConfigurationBlockEntity> {
    @Override
    public void render(MultiblockStructureConfigurationBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (Minecraft.getInstance().player.canUseGameMasterBlocks() || Minecraft.getInstance().player.isSpectator()) {
            BlockPos blockpos = blockEntity.getBlockPos().offset(1, 1, 1);
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
        BlockPos blockpos = blockEntity.getBlockPos();
        BlockPos blockpos1 = blockEntity.getBlockPos().offset(1, 1, 1);

        for (BlockPos blockpos2 : BlockPos.betweenClosed(blockpos1, blockpos1.offset(blockEntity.getStructureSize()).offset(-1, -1, -1))) {
            BlockState blockstate = blockgetter.getBlockState(blockpos2);
            boolean flag = blockstate.isAir();
            boolean flag1 = blockstate.is(Blocks.STRUCTURE_VOID);
            boolean flag2 = blockstate.is(Blocks.BARRIER);
            boolean flag3 = blockstate.is(Blocks.LIGHT);
            boolean flag4 = flag1 || flag2 || flag3;
            if (flag || flag4) {
                float f = flag ? 0.05F : 0.0F;
                double d0 = (float) (blockpos2.getX() - blockpos.getX()) + 0.45F - f;
                double d1 = (float) (blockpos2.getY() - blockpos.getY()) + 0.45F - f;
                double d2 = (float) (blockpos2.getZ() - blockpos.getZ()) + 0.45F - f;
                double d3 = (float) (blockpos2.getX() - blockpos.getX()) + 0.55F + f;
                double d4 = (float) (blockpos2.getY() - blockpos.getY()) + 0.55F + f;
                double d5 = (float) (blockpos2.getZ() - blockpos.getZ()) + 0.55F + f;
                if (flag) {
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer, d0, d1, d2, d3, d4, d5, 0.5F, 0.5F, 1.0F, 1.0F, 0.5F, 0.5F, 1.0F);
                } else if (flag1) {
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer, d0, d1, d2, d3, d4, d5, 1.0F, 0.75F, 0.75F, 1.0F, 1.0F, 0.75F, 0.75F);
                } else if (flag2) {
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer, d0, d1, d2, d3, d4, d5, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F);
                } else {
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer, d0, d1, d2, d3, d4, d5, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
                }
            }
        }
    }

}
