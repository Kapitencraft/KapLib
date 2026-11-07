package net.kapitencraft.kap_lib.multiblock.test.multiblock.structure;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.multiblock.structure.impl.MultiblockStructureBlock;
import net.kapitencraft.kap_lib.multiblock.test.multiblock.entity.TestMultiblockStructureBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TestMultiblockStructureBlock extends MultiblockStructureBlock<TestMultiblockStructureBlockEntity> {
    public TestMultiblockStructureBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable TestMultiblockStructureBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TestMultiblockStructureBlockEntity(blockPos, blockState);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }
}
