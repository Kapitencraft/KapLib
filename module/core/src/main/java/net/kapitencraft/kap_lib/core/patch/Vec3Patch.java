package net.kapitencraft.kap_lib.core.patch;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface Vec3Patch {

    default Vec3 removeScaled(double scale) {
        Vec3 vec3 = (Vec3) this;
        return new Vec3(vec3.x * (1 - scale), vec3.y * (1 - scale), vec3.z * (1 - scale));
    }

    /**
     * moves the given {@code source} vector towards {@code target}
     */
    default Vec3 moveTowards(Vec3 target, double range, boolean percentage) {
        Vec3 source = (Vec3) this;
        Vec3 change = source.subtract(target);
        double dist = source.distanceTo(target);
        return percentage ? change.clampLength(dist * range) : change.clampLength(range);
    }

    default Vec3 clampLength(double value) {
        Vec3 source = (Vec3) this;
        if (source.length() > value) {
            return maxLength(value);
        }
        return minLength(value);
    }

    default Vec3 maxLength(double max) {
        Vec3 source = (Vec3) this;
        if (source.length() < max) {
            return source;
        } else {
            double scale = max / source.length();
            return source.scale(scale);
        }
    }

    default Vec3 minLength(double minimum) {
        Vec3 source = (Vec3) this;
        if (source.length() > minimum) {
            return source;
        } else {
            double scale = minimum / source.length();
            return source.scale(scale);
        }
    }

    /**
     * rotates the given vector around the given pivot on the x-axis
     */
    default Vec3 rotateXAxis(Vec3 pivot, float angle) {
        Vec3 source = (Vec3) this;
        double y = (source.y - pivot.y) * Mth.cos(angle) - (source.z - pivot.z) * Mth.sin(angle) + pivot.y;
        double z = (source.y - pivot.y) * Mth.sin(angle) + (source.z - pivot.z) * Mth.cos(angle) + pivot.z;
        return new Vec3(source.x, y, z);
    }

    /**
     * rotates the given vector around the given pivot on the y-axis
     */
    default Vec3 rotateHorizontalYAxis(@NotNull Vec3 pivot, float angle) {
        Vec3 source = (Vec3) this;
        double x = (source.x - pivot.x) * Mth.cos(angle) - (source.z - pivot.z) * Mth.sin(angle) + pivot.x;
        double z = (source.x - pivot.x) * Mth.sin(angle) + (source.z - pivot.z) * Mth.cos(angle) + pivot.z;
        return new Vec3(x, source.y, z);
    }

    /**
     * rotates the given vector around the given pivot on the z-axis
     */
    default Vec3 rotateZAxis(@NotNull Vec3 pivot, float angle) {
        Vec3 source = (Vec3) this;
        double x = (source.x - pivot.x) * Mth.cos(angle) - (source.y - pivot.y) * Mth.sin(angle) + pivot.x;
        double y = (source.x - pivot.x) * Mth.sin(angle) + (source.y - pivot.y) * Mth.cos(angle) + pivot.y;
        return new Vec3(x, y, source.z);
    }

    /**
     * rotates the given angle around the given axis
     *
     * @param pivot the rotation pivot
     * @param angle the angle in degree
     * @param axis  the axis to rotate around
     * @return the rotated angle
     */
    default Vec3 rotateAroundAxis(@NotNull Vec3 pivot, float angle, @NotNull Direction.Axis axis) {
        angle *= Mth.DEG_TO_RAD;
        return switch (axis) {
            case X -> rotateXAxis(pivot, angle);
            case Y -> rotateHorizontalYAxis(pivot, angle);
            case Z -> rotateZAxis(pivot, angle);
        };
    }
}
