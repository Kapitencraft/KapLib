package net.kapitencraft.kap_lib.multiblock.multiplace.large.part;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public enum Part2Cubed implements MultiplaceBlockPart<Part2Cubed> {
    FRONT_TOP_LEFT(0, 0, 0),
    FRONT_TOP_RIGHT(1, 0, 0),
    FRONT_BOTTOM_LEFT(0, 0, 1),
    FRONT_BOTTOM_RIGHT(1, 0, 1),
    BACK_TOP_LEFT(0, 1, 0),
    BACK_TOP_RIGHT(1, 1, 0),
    BACK_BOTTOM_LEFT(0, 1, 1),
    BACK_BOTTOM_RIGHT(1, 1, 1);

    public static final EnumProperty<Part2Cubed> PROPERTY = EnumProperty.create("part", Part2Cubed.class);

    private final BlockPos offset;

    Part2Cubed(int x, int y, int z) {
        this.offset = new BlockPos(x, y, z);
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
