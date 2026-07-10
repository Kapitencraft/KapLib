package net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.BlockPosRotation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.MultiplaceBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public enum HorizontalOrientation implements MultiblockOrientation<HorizontalOrientation> {
    NORTH_EAST(BlockPosRotation.y270Degrees()),
    NORTH_WEST(BlockPosRotation.y180Degrees()),
    SOUTH_EAST(BlockPosRotation.noRot()),
    SOUTH_WEST(BlockPosRotation.y90Degrees());

    public static final Property<HorizontalOrientation> PROPERTY = EnumProperty.create("orientation", HorizontalOrientation.class);

    private final BlockPosRotation rotation;

    HorizontalOrientation(BlockPosRotation rotation) {
        this.rotation = rotation;
    }

    public static HorizontalOrientation from(Direction one, Direction other) {
        return switch (one) {
            case NORTH -> {
                if (other == Direction.EAST)
                    yield NORTH_EAST;
                yield NORTH_WEST;
            }
            case SOUTH -> {
                if (other == Direction.EAST)
                    yield SOUTH_EAST;
                yield SOUTH_WEST;
            }
            case WEST -> {
                if (other == Direction.NORTH)
                    yield NORTH_WEST;
                yield SOUTH_WEST;
            }
            case EAST -> {
                if (other == Direction.NORTH)
                    yield NORTH_EAST;
                yield SOUTH_EAST;
            }
            default -> throw new IllegalArgumentException("non-horizontal property: " + one);
        };
    }

    @Override
    public BlockPos getPos(MultiplaceBlockPart<?> multiplaceBlockPart) {
        return this.rotation.rotate(multiplaceBlockPart.getOffset());
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase();
    }

    @Override
    public HorizontalOrientation rotate(Rotation rot) {
        return switch (rot) {
            case NONE -> this;
            case CLOCKWISE_90 -> switch (this) {
                case NORTH_EAST -> SOUTH_EAST;
                case NORTH_WEST -> NORTH_EAST;
                case SOUTH_EAST -> SOUTH_WEST;
                case SOUTH_WEST -> NORTH_WEST;
            };
            case CLOCKWISE_180 -> switch (this) {
                case NORTH_EAST -> SOUTH_WEST;
                case NORTH_WEST -> SOUTH_EAST;
                case SOUTH_EAST -> NORTH_WEST;
                case SOUTH_WEST -> NORTH_EAST;
            };
            case COUNTERCLOCKWISE_90 -> switch (this) {
                case NORTH_EAST -> NORTH_WEST;
                case NORTH_WEST -> SOUTH_WEST;
                case SOUTH_EAST -> NORTH_EAST;
                case SOUTH_WEST -> SOUTH_EAST;
            };
        };
    }

    //NORTH & SOUTH = Z = LEFT_RIGHT
    @Override
    public HorizontalOrientation mirror(Mirror mirror) {
        return switch (mirror) {
            case NONE -> this;
            case LEFT_RIGHT -> switch (this) {
                case NORTH_EAST -> SOUTH_EAST;
                case NORTH_WEST -> SOUTH_WEST;
                case SOUTH_EAST -> NORTH_EAST;
                case SOUTH_WEST -> NORTH_WEST;
            };
            case FRONT_BACK -> switch (this) {
                case NORTH_EAST -> NORTH_WEST;
                case NORTH_WEST -> NORTH_EAST;
                case SOUTH_EAST -> SOUTH_WEST;
                case SOUTH_WEST -> SOUTH_EAST;
            };
        };
    }

    @Override
    public BlockPos rotate(BlockPos offset) {
        return rotation.rotate(offset);
    }
}
