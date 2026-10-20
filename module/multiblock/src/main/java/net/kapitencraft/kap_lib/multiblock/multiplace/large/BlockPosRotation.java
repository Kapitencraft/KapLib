package net.kapitencraft.kap_lib.multiblock.multiplace.large;

import net.minecraft.core.BlockPos;

public class BlockPosRotation {
    private final int
            t1, t2, t3,
            m1, m2, m3,
            b1, b2, b3;

    private BlockPosRotation(int t1, int t2, int t3, int m1, int m2, int m3, int b1, int b2, int b3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
    }

    public static BlockPosRotation x90Degrees() {
        return new BlockPosRotation(
                1, 0, 0,
                0, 0, -1,
                0, 1, 0
        );
    }

    public static BlockPosRotation x180Degrees() {
        return new BlockPosRotation(
                0, 0, 0,
                0, -1, 0,
                0, 0, -1
        );
    }

    public static BlockPosRotation x270Degrees() {
        return new BlockPosRotation(
                1, 0, 0,
                0, 0, 1,
                0, -1, 0
        );
    }

    public static BlockPosRotation y90Degrees() {
        return new BlockPosRotation(
                0, 0, -1,
                0, 1, 0,
                1, 0, 0
        );
    }

    public static BlockPosRotation y180Degrees() {
        return new BlockPosRotation(
                -1, 0, 0,
                0, 1, 0,
                0, 0, -1
        );
    }

    public static BlockPosRotation y270Degrees() {
        return new BlockPosRotation(
                0, 0, 1,
                0, 1, 0,
                -1, 0, 0
        );
    }

    public static BlockPosRotation z90Degrees() {
        return new BlockPosRotation(
                0, -1, 0,
                1, 0, 0,
                0, 0, 1
        );
    }

    public static BlockPosRotation z180Degrees() {
        return new BlockPosRotation(
                -1, 0, 0,
                0, -1, 0,
                0, 0, 1
        );
    }

    public static BlockPosRotation z270Degrees() {
        return new BlockPosRotation(
                0, 1, 0,
                -1, 0, 0,
                0, 0, 1
        );
    }

    public static BlockPosRotation noRot() {
        return new BlockPosRotation(
                1, 0, 0,
                0, 1, 0,
                0, 0, 1
        );
    }

    public BlockPosRotation combine(BlockPosRotation other) {
        return new BlockPosRotation(
                t1 * other.t1 + t2 * other.m1 + t3 * other.b1, t1 * other.t2 + t2 * other.m2 + t3 * other.b2, t1 * other.t3 + t2 * other.m3 + t3 * other.b3,
                m1 * other.t1 + m2 * other.m1 + m3 * other.b1, m1 * other.t2 + m2 * other.m2 + m3 * other.b2, m1 * other.t3 + m2 * other.m3 + m3 * other.b3,
                b1 * other.t1 + b2 * other.m1 + b3 * other.b1, b1 * other.t2 + b2 * other.m2 + b3 * other.b2, b1 * other.t3 + b2 * other.m3 + b3 * other.b3
        );
    }

    public BlockPos rotate(BlockPos toRotate) {
        int x = toRotate.getX();
        int y = toRotate.getY();
        int z = toRotate.getZ();
        return new BlockPos(
                t1 * x + t2 * y + t3 * z,
                m1 * x + m2 * y + m3 * z,
                b1 * x + b2 * y + b3 * z
        );
    }

    public BlockPos rotate(BlockPos toRotate, BlockPos pivot) {
        return rotate(toRotate.subtract(pivot)).offset(pivot);
    }
}
