package net.kapitencraft.kap_lib.multiblock.multiplace.line;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public interface MultiplaceLineDirectionalBlock {

    Direction getDirection(BlockState state);

    DirectionProperty getDirectionProperty();

    default BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(getDirectionProperty(), rot.rotate(state.getValue(getDirectionProperty())));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    default BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(getDirection(state)));
    }
}
