package net.kapitencraft.kap_lib.multiblock.multiplace.large.part;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;

public interface MultiplaceBlockPart<P extends MultiplaceBlockPart<P>> extends Comparable<P>, StringRepresentable {

    BlockPos getOffset();
}