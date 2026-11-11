package net.kapitencraft.kap_lib.core.patch;

import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public interface Vec3iPatch {

    /**
     * randomly offsets the given block position horizontally
     */
    default Vec3i randomOffset(RandomSource source) {
        int random = source.nextIntBetweenInclusive(0, 8);
        if (random > 3) random++; //ensure 4 is never hit
        int xOffset = random / 3 - 1;
        int zOffset = random % 3 - 1;
        return ((Vec3i) this).offset(xOffset, 0, zOffset);
    }

    /**
     * scales the given BlockPos around world origin
     *
     * @param scale the scale
     * @return the new, scaled BP
     */
    default Vec3i scale(double scale) {
        throw new AssertionError();
    }

    /**
     * get all positions between this and the given block position
     *
     * @param b         ending position
     * @param thickness thickness of the line
     */
    default List<Vec3i> makeLine(Vec3i b, MathHelper.LineThickness thickness) {
        Vec3i diff = b.subtract((Vec3i) this);
        double horizontal = Mth.sqrt((diff.getX() * diff.getX()) + (diff.getZ() * diff.getZ()) + (diff.getY() * diff.getY()));
        int numPoints = (int) (thickness == MathHelper.LineThickness.THIN ? horizontal * 20 : horizontal * 50);
        List<Vec3i> list = new ArrayList<>();
        for (int i = 0; i < numPoints; i++) {
            double t = i / (numPoints - 1.);
            list.add(((Vec3i) this).offset(diff).scale(t));
        }
        return list;
    }

    default Vec3i with(Direction.Axis axis, int value) {
        Vec3i self = (Vec3i) this;
        int x = axis == Direction.Axis.X ? value : self.getX();
        int y = axis == Direction.Axis.Y ? value : self.getY();
        int z = axis == Direction.Axis.Z ? value : self.getZ();
        return new Vec3i(x, y, z);
    }

    default int volume() {
        Vec3i self = (Vec3i) this;
        return Math.abs(self.getX() * self.getY() * self.getZ());
    }

    default boolean within(Vec3i min, Vec3i max) {
        Vec3i self = (Vec3i) this;
        return min.getX() <= self.getX() && self.getX() <= max.getX() &&
                min.getY() <= self.getY() && self.getY() <= max.getY() &&
                min.getZ() <= self.getZ() && self.getZ() <= max.getZ();
    }

    default String toDimView() {
        Vec3i self = (Vec3i) this;
        return "[" + self.getX() + ", " + self.getY() + ", " + self.getZ() + "]";
    }
}
