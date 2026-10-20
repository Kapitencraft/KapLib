package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.List;

public class BlockMatcher {
    final List<Integer>[] quantifierList;
    int last = 0;
    private final BlockPattern definingPattern;
    private final LevelAccessor blockAccessor;

    BlockMatcher(int quantifierCount, BlockPattern definingPattern, LevelAccessor blockAccessor) {
        quantifierList = new List[quantifierCount];
        this.definingPattern = definingPattern;
        this.blockAccessor = blockAccessor;
        for (int i = 0; i < quantifierCount; i++) {
            quantifierList[i] = new ArrayList<>();
        }
    }

    public boolean matches(Direction direction, BlockPos pos) {
        BlockPattern.BlockAccessor accessor = new BlockPattern.BlockAccessor(direction, pos, blockAccessor);
        return definingPattern.root.matches(this, accessor, 0);
    }
}
