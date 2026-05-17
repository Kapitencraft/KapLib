package net.kapitencraft.kap_lib.multiblock.structure.config;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class MultiblockStructureConfigurationBlock extends Block {
    public MultiblockStructureConfigurationBlock() {
        super(Properties.ofFullCopy(Blocks.STRUCTURE_BLOCK));
    }
}
