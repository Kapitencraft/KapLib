package net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.BlockPosRotation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.MultiplaceBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public enum Orientation implements MultiblockOrientation<Orientation> {
    DOWN_NORTH(BlockPosRotation.y180Degrees().combine(BlockPosRotation.z270Degrees())),
    DOWN_SOUTH(BlockPosRotation.z270Degrees()),
    DOWN_WEST(BlockPosRotation.y90Degrees().combine(BlockPosRotation.z270Degrees())),
    DOWN_EAST(BlockPosRotation.x90Degrees()),
    UP_NORTH(BlockPosRotation.y180Degrees().combine(BlockPosRotation.z90Degrees())),
    UP_SOUTH(BlockPosRotation.z90Degrees()),
    UP_WEST(BlockPosRotation.x270Degrees().combine(BlockPosRotation.y90Degrees())),
    UP_EAST(BlockPosRotation.x270Degrees()),
    NORTH_WEST(BlockPosRotation.y180Degrees()),
    NORTH_EAST(BlockPosRotation.y270Degrees()),
    SOUTH_WEST(BlockPosRotation.y90Degrees()),
    SOUTH_EAST(BlockPosRotation.noRot());

    public static final EnumProperty<Orientation> PROPERTY = EnumProperty.create("orientation", Orientation.class);

    private final BlockPosRotation rotMat;

    Orientation(BlockPosRotation rotMat) {
        this.rotMat = rotMat;
    }

    public BlockPos getPos(MultiplaceBlockPart multiplaceBlockPart) {
        return rotMat.rotate(multiplaceBlockPart.getOffset());
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase();
    }

    public BlockPos rotate(BlockPos offset) {
        return rotMat.rotate(offset);
    }
}
