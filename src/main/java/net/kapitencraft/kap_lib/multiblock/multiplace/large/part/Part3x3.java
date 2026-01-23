package net.kapitencraft.kap_lib.multiblock.multiplace.large.part;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public enum Part3x3 implements MultiplaceBlockPart<Part3x3> {
    TOP_LEFT(0, 0),
    TOP_MIDDLE(1, 0),
    TOP_RIGHT(2, 0),
    MIDDLE_LEFT(0, 1),
    CENTER(1, 1),
    MIDDLE_RIGHT(2, 1),
    BOTTOM_LEFT(0, 2),
    BOTTOM_MIDDLE(1, 2),
    BOTTOM_RIGHT(2, 2);

    public static final EnumProperty<Part3x3> PROPERTY = EnumProperty.create("part", Part3x3.class);

    //offsets are stored in xz format
    private final BlockPos offset;

    Part3x3(int x, int z) {
        offset = new BlockPos(x, 0, z);
    }

    @Override
    public BlockPos getOffset() {
        return offset;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase();
    }
}
