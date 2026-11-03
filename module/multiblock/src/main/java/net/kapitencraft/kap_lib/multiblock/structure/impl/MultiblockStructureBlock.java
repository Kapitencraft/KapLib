package net.kapitencraft.kap_lib.multiblock.structure.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @param <T> the type of MultiblockStructure this block uses
 */
public abstract class MultiblockStructureBlock<T extends MultiblockStructureBE> extends BaseEntityBlock {

    protected MultiblockStructureBlock(Properties properties) {
        super(properties);
    }

    @Override
    public abstract @Nullable T newBlockEntity(BlockPos blockPos, BlockState blockState);
}
