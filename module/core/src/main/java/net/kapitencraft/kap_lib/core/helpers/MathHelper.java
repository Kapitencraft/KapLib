package net.kapitencraft.kap_lib.core.helpers;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface MathHelper {
    /**
     * A random source. use provided ones wherever possible
     */
    RandomSource RANDOM_SOURCE = RandomSource.create();

    double DAMAGE_CALCULATION_VALUE = 50;

    /**
     * merge the given r, g, b and a values into a packed integer
     */
    static int ARGBtoInt(int a, int r, int g, int b) {
        int returnable = (a << 8) + r;
        returnable = (returnable << 8) + g;
        return (returnable << 8) + b;
    }

    static IntSet intSetRange(int min, int max) {
        int[] range = new int[max - min + 1];
        for (int i = min; i <= max; i++) {
            range[i - min] = min + i;
        }
        return IntSet.of(range);
    }

    /**
     * creates a vector in clockwise order perpendicular to the original one
     */
    @SuppressWarnings("SuspiciousNameCombination")
    static Vec2 normal(Vec2 og) {
        return new Vec2(og.y, -1 / og.x);
    }

    /**
     * round the given number to the given number of decimal digits
     *
     * @param no  the given number
     * @param num the number of decimal digits
     * @return the rounded number
     */
    static double round(double no, int num) {
        return Math.floor(no * Math.pow(10, num)) / (Math.pow(10, num));
    }

    /**
     * default rounding, using 2 decimal digits
     *
     * @see MathHelper#round(double, int)
     */
    static double defRound(double no) {
        return round(no, 2);
    }

    static double shortRound(double no) {
        return round(no, 1);
    }

    /**
     * updated damage calculation:
     */
    static float calculateDamage(float damage, double armorValue, double armorToughnessValue) {
        double f = DAMAGE_CALCULATION_VALUE - armorToughnessValue / 4.0F;
        double defencePercentage = armorValue / (armorValue + f);
        return (float) (damage * (1f - defencePercentage));
    }

    /**
     * gives the 3d location for the given arm and entity
     */
    static Vec3 getHandHoldingItemAngle(HumanoidArm arm, @NotNull Entity entity) {
        return entity.position().add(entity.calculateViewVector(0.0F, entity.getYRot() + (float) (arm == HumanoidArm.RIGHT ? 80 : -80)).scale(0.5D));
    }

    /**
     * rotates the given vector around the given pivot on the x-axis
     * @deprecated use patch method instead
     */
    @Deprecated
    static Vec3 rotateXAxis(@NotNull Vec3 source, Vec3 pivot, float angle) {
        double y = (source.y - pivot.y) * Mth.cos(angle) - (source.z - pivot.z) * Mth.sin(angle) + pivot.y;
        double z = (source.y - pivot.y) * Mth.sin(angle) + (source.z - pivot.z) * Mth.cos(angle) + pivot.z;
        return new Vec3(source.x, y, z);
    }

    /**
     * rotates the given vector around the given pivot on the y-axis
     * @deprecated use patch method instead
     */
    @Deprecated
    static Vec3 rotateHorizontalYAxis(@NotNull Vec3 source, @NotNull Vec3 pivot, float angle) {
        double x = (source.x - pivot.x) * Mth.cos(angle) - (source.z - pivot.z) * Mth.sin(angle) + pivot.x;
        double z = (source.x - pivot.x) * Mth.sin(angle) + (source.z - pivot.z) * Mth.cos(angle) + pivot.z;
        return new Vec3(x, source.y, z);
    }

    /**
     * rotates the given vector around the given pivot on the z-axis
     * @deprecated use patch method instead
     */
    @Deprecated
    static Vec3 rotateZAxis(@NotNull Vec3 source, @NotNull Vec3 pivot, float angle) {
        double x = (source.x - pivot.x) * Mth.cos(angle) - (source.y - pivot.y) * Mth.sin(angle) + pivot.x;
        double y = (source.x - pivot.x) * Mth.sin(angle) + (source.y - pivot.y) * Mth.cos(angle) + pivot.y;
        return new Vec3(x, y, source.z);
    }

    /**
     * rotates the given angle around the given axis
     *
     * @param source the source Vec to rotate
     * @param pivot  the rotation pivot
     * @param angle  the angle in degree
     * @param axis   the axis to rotate around
     * @return the rotated angle
     * @deprecated use patch method instead
     */
    @Deprecated
    static Vec3 rotateAroundAxis(@NotNull Vec3 source, @NotNull Vec3 pivot, float angle, @NotNull Direction.Axis axis) {
        angle *= Mth.DEG_TO_RAD;
        return switch (axis) {
            case X -> source.rotateXAxis(pivot, angle);
            case Y -> source.rotateHorizontalYAxis(pivot, angle);
            case Z -> source.rotateZAxis(pivot, angle);
        };
    }

    /**
     * @return if the given {@code val} value is between {@code start} and {@code end}
     */
    static boolean isBetween(double val, int start, int end) {
        return Mth.clamp(val, start, end) == val;
    }

    static boolean isBetween(double val, double start, double end) {
        return Mth.clamp(val, start, end) == val;
    }

    /**
     * @return if the given values are between the given start and end values
     * @see MathHelper#isBetween(double, int, int) isBetween
     */
    static boolean is2dBetween(double xVal, double yVal, int xStart, int yStart, int xEnd, int yEnd) {
        return isBetween(xVal, xStart, xEnd) && isBetween(yVal, yStart, yEnd);
    }

    /**
     * @return the biggest integer from the map
     */
    static int getLargest(@NotNull Collection<Integer> floats) {
        return floats.stream().mapToInt(i -> i).max().orElse(0);
    }

    /**
     * checks any string if it's a number between {@code min} and {@code max}
     */
    @Contract("_, _ -> new")
    static Predicate<String> checkForInteger(int min, int max) {
        return s -> {
            try {
                int num = Integer.parseInt(s);
                return num >= min && num <= max;
            } catch (Exception e) {
                return false;
            }
        };
    }

    /**
     * @return a list of entities that surround the given source in a {@code range} radius and is an instance of {@code tClass}
     * @deprecated use patch method instead
     */
    @Deprecated
    static <T extends Entity> List<T> getEntitiesAround(Class<T> tClass, Entity source, double range) {
        return source.getEntitiesAround(tClass, range);
    }

    @Contract("_, null, _ -> fail; null, _, _ -> fail")
    static void add(Supplier<Integer> getter, Consumer<Integer> setter, int change) {
        setter.accept(getter.get() + change);
    }

    /**
     * @return a list of locations in the line of sight ot the given entity
     * @deprecated use patch method instead
     */
    @Deprecated
    @Contract("null, _, _ -> fail; _, _, _ -> new")
    static ArrayList<Vec3> lineOfSight(Entity entity, double range, double scaling) {
        Vec3 viewVec = entity.calculateViewVector(entity.getXRot(), entity.getYRot());
        Vec3 viewVecWithLoc = viewVec.add(entity.getEyePosition());
        Vec3 end = viewVec.scale(range).add(entity.getEyePosition());
        BlockHitResult result = entity.level().clip(new ClipContext(viewVecWithLoc, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        Vec3 diff = result.getLocation().subtract(viewVecWithLoc);
        ArrayList<Vec3> list = new ArrayList<>();
        for (int i = 0; i < diff.length() / scaling; i++) {
            list.add(clampLength(diff, i * scaling).add(entity.getEyePosition()));
        }
        return list;
    }

    /**
     * @return a list of locations in the line of sight of the position and rotation
     */
    static ArrayList<Vec3> lineOfSight(Vec2 vec, Vec3 pos, double range, double scaling) {
        ArrayList<Vec3> line = new ArrayList<>();
        Vec3 vec3;
        for (double i = 0; i <= range; i += scaling) {
            vec3 = calculateViewVector(vec.x, vec.y).scale(i).add(pos.x, pos.y, pos.z);
            line.add(vec3);
        }
        return line;
    }

    /**
     * count all elements inside the collection
     */
    static int count(@NotNull Collection<Integer> collection) {
        int count = 0;
        for (Integer integer : collection) {
            count += integer;
        }
        return count;
    }

    /**
     * get all positions between the 2 given block positions
     * @param a starting position
     * @param b ending position
     * @param thickness thickness of the line
     */
    static List<BlockPos> makeLine(BlockPos a, BlockPos b, LineThickness thickness) {
        BlockPos diff = b.subtract(a);
        double horizontal = Mth.sqrt((diff.getX() * diff.getX()) + (diff.getZ() * diff.getZ()) + (diff.getY() * diff.getY()));
        int numPoints = (int) (thickness == LineThickness.THIN ? horizontal * 20 : horizontal * 50);
        List<BlockPos> list = new ArrayList<>();
        for (int i = 0; i < numPoints; i++) {
            double t = i / (numPoints - 1.);
            list.add(makeLinePos(t, a, diff));
        }
        return list;
    }

    /**
     * @return get the position relative multiplied by {@code t}
     */
    @ApiStatus.Internal
    private static BlockPos makeLinePos(double t, BlockPos a, BlockPos diff) {
        return new BlockPos((int) (a.getX() + diff.getX() * t), (int) (a.getY() + diff.getY() * t), (int) (a.getZ() + diff.getZ() * t));
    }

    /**
     * creates a list of vectors that are all positioned on the given line with given spacing
     */
    static List<Vec3> makeLine(Vec3 a, Vec3 b, float spacing) {
        Vec3 diff = b.subtract(a);
        int numPoints = (int) (diff.length() / spacing);
        List<Vec3> list = new ArrayList<>();
        for (int i = 0; i < numPoints; i++) {
            double t = i / (numPoints - 1.);
            list.add(a.add(diff.scale(t)));
        }
        return list;
    }

    /**
     * validates that the given index is a valid index into the given array (it being non-negative, smaller than the size of the list)
     */
    static <T> boolean validIndex(List<T> values, int selectedIndex) {
        return isBetween(selectedIndex, 0, values.size() - 1);
    }

    /**
     * adds roll rotation to the given rotation vector
     */
    static Vec3 withRoll(Vec2 rotation, float roll) {
        return new Vec3(rotation.x, rotation.y, roll);
    }

    enum LineThickness {
        THIN,
        THICK
    }

    /**
     * moves the given {@code source} vector towards {@code target}
     * @deprecated use patch method instead
     */
    @Deprecated
    static Vec3 moveTowards(Vec3 source, Vec3 target, double range, boolean percentage) {
        return source.moveTowards(target, range, percentage);
    }

    /**
     * picks a random element from the list
     */
    @Nullable
    static <T> T pickRandom(@NotNull List<T> list) {
        return pickRandom(list, RANDOM_SOURCE);
    }

    /**
     * picks a random element from the list using the given {@link RandomSource}
     */
    static <T> T pickRandom(@NotNull List<T> list, @NotNull RandomSource source) {
        return list.isEmpty() ? null : list.get(Mth.nextInt(source, 0, list.size() - 1));
    }

    /**
     * @return whether the chance fired, checking for the given entities Luck value, if present
     */
    static boolean chance(double baseChance, @Nullable Entity entity) {
        if (entity instanceof LivingEntity living) {
            return chance(baseChance, living);
        } else {
            return chance(baseChance, null);
        }
    }

    /**
     * @return whether the chance fired, checking for the {@link LivingEntity }
     */
    static boolean chance(double chance, @Nullable LivingEntity living) {
        return Math.random() <= chance * (living != null && living.getAttributes().hasAttribute(Attributes.LUCK) ? (1 + living.getAttributeValue(Attributes.LUCK) / 100) : 1);
    }

    /**
     * gets all entities inside the given AABB source of the given class inside the given level
     * @deprecated use patch method instead
     */
    @Deprecated
    static <T extends Entity> List<T> getEntitiesAround(Class<T> tClass, Level level, AABB source, double range) {
        return level.getEntitiesAround(tClass, source, range);
    }

    /**
     * @return the closest entity of the given type, or null if none could be found within the given range
     * @deprecated use patch method instead
     */
    @Deprecated
    static <T extends Entity> @Nullable T getClosestEntity(Class<T> tClass, Entity source, double range) {
        return source.getClosestEntity(tClass, range);
    }

    /**
     * @param type the type of entity to search for
     * @param origin the entity to search from
     * @param range the maximum distance to check in
     * @param filter filter for which entities are eligible
     * @return the closest entity, or null, if no eligible entity was found
     * @deprecated use patch method instead
     */
    @Deprecated
    static <T extends Entity> @Nullable T getClosestEntity(Class<T> type, Entity origin, double range, Predicate<T> filter) {
        return origin.getClosestEntity(type, range, filter);
    }

    /**
     * gets the closest living entity
     *
     * @see MathHelper#getClosestEntity(Class, Entity, double)
     */
    static LivingEntity getClosestLiving(Entity source, double range) {
        return source.getClosestLiving(range);
    }

    /**
     * get Living entities around the given source
     *
     * @see MathHelper#getEntitiesAround(Class, Entity, double)
     */

    static List<LivingEntity> getLivingAround(Entity source, double range) {
        return source.getLivingAround(range);
    }

    /**
     * calculates the view vector of the given x and y rotation
     */
    static Vec3 calculateViewVector(float horizontalHeightXAxis, float verticalYAxis) {
        float f = horizontalHeightXAxis * ((float) Math.PI / 180F);
        float f1 = -verticalYAxis * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    /**
     * gets a list of any entity of the given class
     */
    static <T extends Entity> List<T> getAllEntitiesInsideCone(Class<T> tClass, float span, double range, Vec3 sourcePos, Vec2 sourceRot, Level level) {
        double halfSpan = span / 2;
        double incremental = Math.sin(halfSpan) * 0.1;
        List<Vec3> lineOfSight = lineOfSight(sourceRot, sourcePos, range, 0.1);
        List<T> toReturn = new ArrayList<>();
        lineOfSight.stream().collect(CollectorHelper.toMapForKeys(lineOfSight::indexOf))
                .forEach((integer, vec3) -> toReturn.addAll(getEntitiesAround(tClass, level, vec3, incremental * integer).stream().filter(entity -> !toReturn.contains(entity)).toList()));
        return toReturn;
    }

    /**
     * gets all entities within a cube around the middle point and range
     *
     * @param loc   the middle point of the area
     * @param range the radius of the cube
     */
    static <T extends Entity> List<T> getEntitiesAround(Class<T> tClass, Level level, Vec3 loc, double range) {
        return level.getEntitiesOfClass(tClass, new AABB(loc.x - range, loc.y - range, loc.z - range, loc.x + range, loc.y + range, loc.z + range));
    }

    /**
     * gets all entities within a cylinder
     *
     * @param radius    the radius of the cylinder
     * @param sourcePos the bottom center of the cylinder
     * @param rot       the rotation of the cylinder
     * @param height    the height of the cylinder
     */
    static List<Entity> getAllEntitiesInsideCylinder(float radius, Vec3 sourcePos, Vec2 rot, double height, Level level) {
        List<Entity> toReturn = new ArrayList<>();
        ArrayList<Vec3> lineOfSight = lineOfSight(rot, sourcePos, height, 0.1);
        lineOfSight.forEach(vec3 -> {
            List<Entity> entities = getEntitiesAround(Entity.class, level, vec3, radius);
            toReturn.addAll(entities.stream().filter(entity -> !toReturn.contains(entity)).toList());
        });
        return toReturn;
    }

    /**
     * calculates the necessary rotation vector for the source entity to look at the target entity pivoted at their feet
     */
    static Vec2 createTargetRotation(Entity source, Entity target) {
        return createTargetRotationFromPos(source.position(), target.position());
    }

    /**
     * calculates the necessary rotation vector for the source vector to look at the target vector
     */
    static Vec2 createTargetRotationFromPos(@NotNull Vec3 source, @NotNull Vec3 target) {
        double dX = target.x - source.x;
        double dY = target.y - source.y;
        double dZ = target.z - source.z;
        double d3 = Math.sqrt(dX * dX + dZ * dZ);
        return new Vec2(Mth.wrapDegrees((float) (-(Mth.atan2(dY, d3) * (double) (180F / (float) Math.PI)))), Mth.wrapDegrees((float) (Mth.atan2(dZ, dX) * (double) (180F / (float) Math.PI)) - 90.0F));
    }

    /**
     * same as createTargetRotation but using eye position instead of feet position
     */
    static Vec2 createTargetRotationFromEyeHeight(Entity source, Entity target) {
        return createTargetRotationFromPos(source.getEyePosition(), target.getEyePosition());
    }

    /**
     * checks whether the given source entity is behind the given target entity
     * @deprecated use patch method instead
     */
    @Deprecated
    static boolean isBehind(Entity source, Entity target) {
        return source.isBehind(target);
    }

    /**
     * ensures the given vectors length is never below the given value
     * @param source the vector to clamp
     * @param minimum the maximum allowed length
     * @return the new vec
     * @deprecated use patch method instead
     */
    @Deprecated
    @Contract("null, _ -> fail")
    static Vec3 minimiseLength(Vec3 source, double minimum) {
        return source.minLength(minimum);
    }

    /**
     * ensures the given vectors length never exceeds the given value
     * @param source the vector to clamp
     * @param maximum the maximum allowed length
     * @return the new vec
     * @deprecated use patch method instead
     */
    @Deprecated
    @Contract("null, _ -> fail")
    static Vec3 maximiseLength(Vec3 source, double maximum) {
        return source.maxLength(maximum);
    }

    @Contract("null, _ -> fail")
    static Vec3 clampLength(Vec3 source, double value) {
        return source.clampLength(value);
    }

    /**
     * @param vec3 the position to remove from
     * @param scale the percentage to remove
     * @return the scaled vec
     * @deprecated use patch method instead
     */
    @Deprecated
    @Contract("null, _ -> fail")
    static Vec3 removeByScale(Vec3 vec3, double scale) {
        return vec3.removeScaled(scale);
    }

    /**
     * returns a float within the given range
     */
    static float randomBetween(RandomSource source, float min, float max) {
        return Mth.lerp(source.nextFloat(), min, max);
    }

    /**
     * returns a double within the given range
     */
    static double randomBetween(RandomSource source, double min, double max) {
        return Mth.lerp(source.nextDouble(), min, max);
    }

    /**
     * returns a random position within the given box spanned by the two vectors as opposing corners
     */
    static Vec3 randomBetween(RandomSource source, Vec3 min, Vec3 max) {
        return new Vec3(
                randomBetween(source, min.x, max.x),
                randomBetween(source, min.y, max.y),
                randomBetween(source, min.z, max.z)
        );
    }

    /**
     * returns a random position within the given bounding box
     * @deprecated use patch method instead
     */
    @Deprecated
    static Vec3 randomIn(RandomSource source, AABB box) {
        return box.randomIn(source);
    }

    /**
     * picks the largest of the given values or -1 if the array is empty
     */
    static float pickLargest(float... values) {
        Float min = null;
        for (float f : values) {
            if (min == null || min < f) {
                min = f;
            }
        }
        return min == null ? -1 : min;
    }

    /**
     * randomly offsets the given block position horizontally
     */
    static BlockPos randomOffset(BlockPos pivot, RandomSource source) {
        int random = source.nextIntBetweenInclusive(0, 8);
        if (random > 3) random++; //ensure 4 is never hit
        int xOffset = random / 3 - 1;
        int zOffset = random % 3 - 1;
        return pivot.offset(xOffset, 0, zOffset);
    }
}