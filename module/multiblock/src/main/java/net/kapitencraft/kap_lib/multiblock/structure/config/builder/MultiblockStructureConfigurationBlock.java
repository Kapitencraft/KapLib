package net.kapitencraft.kap_lib.multiblock.structure.config.builder;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MultiblockStructureConfigurationBlock extends BaseEntityBlock {

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    public MultiblockStructureConfigurationBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MultiblockStructureConfigurationBlockEntity(pos, state);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof MultiblockStructureConfigurationBlockEntity mscbe) {
            return mscbe.usedBy(player) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
