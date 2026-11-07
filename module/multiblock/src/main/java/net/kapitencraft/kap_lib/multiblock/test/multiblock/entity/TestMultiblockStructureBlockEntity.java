package net.kapitencraft.kap_lib.multiblock.test.multiblock.entity;

import net.kapitencraft.kap_lib.multiblock.structure.impl.MultiblockStructureBE;
import net.kapitencraft.kap_lib.multiblock.test.registry.TestBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TestMultiblockStructureBlockEntity extends MultiblockStructureBE {
    public TestMultiblockStructureBlockEntity(BlockPos pos, BlockState blockState) {
        super(TestBlockEntityTypes.MULTIBLOCK_STRUCTURE_TEST.get(), pos, blockState);
    }
}