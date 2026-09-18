package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.Quantifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class MultiblockStructureMatcher {
    private final MultiblockStructureConfiguration structure;

    public MultiblockStructureMatcher(MultiblockStructureConfiguration structure) {
        this.structure = structure;
    }

    public MultiblockStructureConfiguration getStructure() {
        return structure;
    }

}
