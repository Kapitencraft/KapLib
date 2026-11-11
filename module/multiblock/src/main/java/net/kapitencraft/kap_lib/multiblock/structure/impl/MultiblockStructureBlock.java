package net.kapitencraft.kap_lib.multiblock.structure.impl;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.multiblock.structure.match.MultiblockStructureMatch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @param <T> the type of MultiblockStructure this block uses
 */
public abstract class MultiblockStructureBlock<T extends MultiblockStructureBE> extends BaseEntityBlock {

    protected MultiblockStructureBlock(Properties properties) {
        super(properties);
    }

    @Override
    public abstract @Nullable T newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState);

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if (!newState.is(this)) {
            T entity = (T) level.getBlockEntity(pos);
            MultiblockStructureBE manager = entity.getManager();
            if (manager == null)
                LibConstants.LOGGER.warn("detected removal of multiblock part without valid manager!");
            else
                manager.onPartRemoved(pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (blockEntity instanceof MultiblockStructureBE multiblockStructureBE) {
            BlockState originalState = multiblockStructureBE.getOriginalState();
            originalState.getBlock().playerDestroy(level, player, pos, originalState, null, tool);
        } else {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        }
    }
}
