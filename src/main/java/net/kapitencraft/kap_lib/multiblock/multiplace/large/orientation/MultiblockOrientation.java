package net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.MultiplaceBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public interface MultiblockOrientation<O> extends StringRepresentable, Comparable<O> {

    BlockPos getPos(MultiplaceBlockPart multiplaceBlockPart);

    static <O extends MultiblockOrientation<O>> @NotNull O getOrientation(@NotNull BlockPlaceContext context, Property<O> property) {
        Direction horizontal = context.getHorizontalDirection();
        if (property == HorizontalOrientation.PROPERTY) {
            return (O) HorizontalOrientation.from(horizontal, horizontal.getClockWise());
        }
        return null;
    }
}