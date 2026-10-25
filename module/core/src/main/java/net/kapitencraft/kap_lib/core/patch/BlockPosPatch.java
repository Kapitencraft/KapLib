package net.kapitencraft.kap_lib.core.patch;

import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public interface BlockPosPatch {

    /**
     * randomly offsets the given block position horizontally
     */
    default BlockPos randomOffset(RandomSource source) {
        int random = source.nextIntBetweenInclusive(0, 8);
        if (random > 3) random++; //ensure 4 is never hit
        int xOffset = random / 3 - 1;
        int zOffset = random % 3 - 1;
        return ((BlockPos) this).offset(xOffset, 0, zOffset);
    }

    /**
     * scales the given BlockPos around world origin
     *
     * @param scale the scale
     * @return the new, scaled BP
     */
    default BlockPos scale(double scale) {
        throw new AssertionError();
    }

    /**
     * get all positions between the 2 given block positions
     *
     * @param a         starting position
     * @param b         ending position
     * @param thickness thickness of the line
     */
    default List<BlockPos> makeLine(BlockPos a, BlockPos b, MathHelper.LineThickness thickness) {
        BlockPos diff = b.subtract(a);
        double horizontal = Mth.sqrt((diff.getX() * diff.getX()) + (diff.getZ() * diff.getZ()) + (diff.getY() * diff.getY()));
        int numPoints = (int) (thickness == MathHelper.LineThickness.THIN ? horizontal * 20 : horizontal * 50);
        List<BlockPos> list = new ArrayList<>();
        for (int i = 0; i < numPoints; i++) {
            double t = i / (numPoints - 1.);
            list.add(a.offset(diff).scale(t));
        }
        return list;
    }

    default BlockPos with(Direction.Axis axis, int value) {
        BlockPos self = (BlockPos) this;
        int x = axis == Direction.Axis.X ? value : self.getX();
        int y = axis == Direction.Axis.Y ? value : self.getY();
        int z = axis == Direction.Axis.Z ? value : self.getZ();
        return new BlockPos(x, y, z);
    }

    default int volume() {
        BlockPos self = (BlockPos) this;
        return Math.abs(self.getX() * self.getY() * self.getZ());
    }
}
