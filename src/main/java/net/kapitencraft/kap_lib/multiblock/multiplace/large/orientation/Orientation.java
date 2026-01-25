package net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.BlockPosRotation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.MultiplaceBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
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

    @Override
    public Orientation rotate(Rotation rot) {
        return switch (rot) {
            case NONE -> this;
            case CLOCKWISE_90 -> switch (this) {
                case DOWN_NORTH -> DOWN_EAST;
                case DOWN_SOUTH -> DOWN_WEST;
                case DOWN_WEST -> DOWN_NORTH;
                case DOWN_EAST -> DOWN_SOUTH;
                case UP_NORTH -> UP_EAST;
                case UP_SOUTH -> UP_WEST;
                case UP_WEST -> UP_NORTH;
                case UP_EAST -> UP_SOUTH;
                case NORTH_EAST -> SOUTH_EAST;
                case NORTH_WEST -> NORTH_EAST;
                case SOUTH_EAST -> SOUTH_WEST;
                case SOUTH_WEST -> NORTH_WEST;
            };
            case CLOCKWISE_180 -> switch (this) {
                case DOWN_NORTH -> DOWN_SOUTH;
                case DOWN_SOUTH -> DOWN_NORTH;
                case DOWN_WEST -> DOWN_EAST;
                case DOWN_EAST -> DOWN_WEST;
                case UP_NORTH -> UP_SOUTH;
                case UP_SOUTH -> UP_NORTH;
                case UP_WEST -> UP_EAST;
                case UP_EAST -> UP_WEST;
                case NORTH_EAST -> SOUTH_WEST;
                case NORTH_WEST -> SOUTH_EAST;
                case SOUTH_EAST -> NORTH_WEST;
                case SOUTH_WEST -> NORTH_EAST;
            };
            case COUNTERCLOCKWISE_90 -> switch (this) {
                case DOWN_NORTH -> DOWN_WEST;
                case DOWN_SOUTH -> DOWN_EAST;
                case DOWN_WEST -> DOWN_SOUTH;
                case DOWN_EAST -> DOWN_NORTH;
                case UP_NORTH -> UP_WEST;
                case UP_SOUTH -> UP_EAST;
                case UP_WEST -> UP_SOUTH;
                case UP_EAST -> UP_NORTH;
                case NORTH_EAST -> NORTH_WEST;
                case NORTH_WEST -> SOUTH_WEST;
                case SOUTH_EAST -> NORTH_EAST;
                case SOUTH_WEST -> SOUTH_EAST;
            };
        };
    }

    //NORTH & SOUTH = Z = LEFT_RIGHT
    @Override
    public Orientation mirror(Mirror mirror) {
        return switch (mirror) {
            case NONE -> this;
            case LEFT_RIGHT -> switch (this) {
                case DOWN_NORTH -> DOWN_SOUTH;
                case DOWN_SOUTH -> DOWN_NORTH;
                case DOWN_WEST, UP_WEST, DOWN_EAST, UP_EAST -> this;
                case UP_NORTH -> UP_SOUTH;
                case UP_SOUTH -> UP_NORTH;
                case NORTH_WEST -> SOUTH_WEST;
                case NORTH_EAST -> SOUTH_EAST;
                case SOUTH_WEST -> NORTH_WEST;
                case SOUTH_EAST -> NORTH_EAST;
            };
            case FRONT_BACK -> switch (this) {
                case DOWN_NORTH, UP_NORTH, DOWN_SOUTH, UP_SOUTH -> this;
                case DOWN_WEST -> DOWN_EAST;
                case DOWN_EAST -> DOWN_WEST;
                case UP_WEST -> UP_EAST;
                case UP_EAST -> UP_WEST;
                case NORTH_WEST -> NORTH_EAST;
                case NORTH_EAST -> NORTH_WEST;
                case SOUTH_WEST -> SOUTH_EAST;
                case SOUTH_EAST -> SOUTH_WEST;
            };
        };
    }
}
