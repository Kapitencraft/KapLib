package net.kapitencraft.kap_lib.multiblock.multiplace.large.part;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public enum Part2x3 implements MultiplaceBlockPart<Part2x3> {
    TOP_LEFT(0, 0),
    TOP_RIGHT(1, 0),
    MIDDLE_LEFT(0, 1),
    MIDDLE_RIGHT(1, 1),
    BOTTOM_LEFT(0, 2),
    BOTTOM_RIGHT(1, 2);

    public static final EnumProperty<Part2x3> PROPERTY = EnumProperty.create("part", Part2x3.class);

    //offsets are stored in xz format
    private final BlockPos offset;

    Part2x3(int x, int z) {
        this.offset = new BlockPos(x, 0, z);
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
