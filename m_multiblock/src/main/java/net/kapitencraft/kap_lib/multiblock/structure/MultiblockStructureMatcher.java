package net.kapitencraft.kap_lib.multiblock.structure;

import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;

public class MultiblockStructureMatcher {
    private final MultiblockStructureConfiguration structure;

    public MultiblockStructureMatcher(MultiblockStructureConfiguration structure) {
        this.structure = structure;
    }

    public boolean matches(ServerLevel level, BlockPos origin) {
        Vec3i size = structure.getSize();
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    if (!structure.matches(x, y, z, level.getBlockState(origin.offset(x, y, z)))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public MultiblockStructureConfiguration getStructure() {
        return structure;
    }
}
