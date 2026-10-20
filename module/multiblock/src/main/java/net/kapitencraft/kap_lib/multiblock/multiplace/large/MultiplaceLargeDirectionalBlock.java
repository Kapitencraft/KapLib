package net.kapitencraft.kap_lib.multiblock.multiplace.large;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.MultiblockOrientation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public interface MultiplaceLargeDirectionalBlock<O extends MultiblockOrientation<O>> {

    Property<O> getOrientationProperty();

    default @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(getOrientationProperty(), state.getValue(getOrientationProperty()).rotate(rot));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    default @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(getOrientationProperty(), state.getValue(getOrientationProperty()).mirror(mirror));
    }
}
