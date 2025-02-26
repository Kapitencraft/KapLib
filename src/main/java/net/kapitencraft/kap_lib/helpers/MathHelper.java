package net.kapitencraft.kap_lib.helpers;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.util.Reference;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface MathHelper {

    double DAMAGE_CALCULATION_VALUE = 50;

    /**
     * merge the given r, g, b and a values into a packed integer
     */
    static int RGBAtoInt(int r, int g, int b, int a) {
        int returnable = (a << 8) + r;
        returnable = (returnable << 8) + g;
        return (returnable << 8) + b;
    }

    static IntSet intSetRange(int min, int max) {
        int[] range = new int[max - min + 1];
        for (int i = min; i <= max; i++) {
            range[i-min] = min + i;
        }
        return IntSet.of(range);
    }

    /**
     * round the given number to the given number of decimal digits
     * @param no the given number
     * @param num the number of decimal digits
     * @return the rounded number
     */
    static double round(double no, int num) {
        return Math.floor(no * Math.pow(10, num)) / (Math.pow(10, num));
    }

    /**
     * default rounding, using 2 decimal digits
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
        return entity.position().add(entity.calculateViewVector(0.0F, entity.getYRot() + (float)(arm == HumanoidArm.RIGHT ? 80 : -80)).scale(0.5D));
    }

    static Vec3 rotateXAxis(@NotNull Vec3 source, Vec3 pivot, float angle) {
        double y = (source.y - pivot.y) * Mth.cos(angle) - (source.z - pivot.z) * Mth.sin(angle) + pivot.y;
        double z = (source.y - pivot.y) * Mth.sin(angle) + (source.z - pivot.z) * Mth.cos(angle) + pivot.z;
        return new Vec3(source.x, y, z);
    }

    /**
     * @param source the source Vec to rotate
     * @param pivot the rotation pivot
     * @param angle the angle in radians
     * @return the rotated angle
     */
    static Vec3 rotateHorizontalYAxis(@NotNull Vec3 source, @NotNull Vec3 pivot, float angle) {
        double x = (source.x - pivot.x) * Mth.cos(angle) - (source.z - pivot.z) * Mth.sin(angle) + pivot.x;
        double z = (source.x - pivot.x) * Mth.sin(angle) + (source.z - pivot.z) * Mth.cos(angle) + pivot.z;
        return new Vec3(x, source.y, z);
    }

    static Vec3 rotateZAxis(@NotNull Vec3 source, @NotNull Vec3 pivot, float angle) {
        double x = (source.x - pivot.x) * Mth.cos(angle) - (source.y - pivot.y) * Mth.sin(angle) + pivot.x;
        double y = (source.x - pivot.x) * Mth.sin(angle) + (source.y - pivot.y) * Mth.cos(angle) + pivot.y;
        return new Vec3(x, y, source.z);
    }

    /**
     * rotates the given angle around the given axis
     * @param source the source Vec to rotate
     * @param pivot the rotation pivot
     * @param angle the angle in degree
     * @param axis the axis to rotate around
     * @return the rotated angle
     */
    static Vec3 rotateAroundAxis(@NotNull Vec3 source, @NotNull Vec3 pivot, float angle, @NotNull Direction.Axis axis) {
        angle *= Mth.DEG_TO_RAD;
        return switch (axis) {
            case X -> rotateXAxis(source, pivot, angle);
            case Y -> rotateHorizontalYAxis(source, pivot, angle);
            case Z -> rotateZAxis(source, pivot, angle);
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
     */
    static <T extends Entity> List<T> getEntitiesAround(Class<T> tClass, Entity source, double range) {
        Level level = source.level();
        return getEntitiesAround(tClass, level, source.getBoundingBox(), range);
    }


    @Contract("_, null, _ -> fail; null, _, _ -> fail")
    static void add(Supplier<Integer> getter, Consumer<Integer> setter, int change) {
        setter.accept(getter.get() + change);
    }

    static void up1(Reference<Integer> reference) {
        add(reference::getIntValue, reference::setValue, 1);
    }

    static void mul(Supplier<Integer> getter, Consumer<Integer> setter, int mul) {
        setter.accept(getter.get() * mul);
    }

    static void mul(Supplier<Double> getter, Consumer<Double> setter, double mul) {
        setter.accept(getter.get() * mul);
    }

    static void mul(Supplier<Float> getter, Consumer<Float> setter, float mul) {
        setter.accept(getter.get() * mul);
    }


    /**
     * @return a list of locations in the line of sight ot the given entity
     */
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
        for (double i = 0; i <= range; i+=scaling) {
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
     */
    static List<BlockPos> makeLine(BlockPos a, BlockPos b, LineSize size) {
        BlockPos diff = b.subtract(a);
        double horizontal = Mth.sqrt((diff.getX() * diff.getX()) + (diff.getZ() * diff.getZ()) + (diff.getY() * diff.getY()));
        int numPoints = (int) (size == LineSize.THIN ? horizontal * 20 : horizontal * 50);
        List<BlockPos> list = new ArrayList<>();
        MiscHelper.repeat(numPoints, integer -> {
            double t = integer / (numPoints - 1.);
            list.add(makeLinePos(t, a, diff));
        });
        return list;
    }

    /**
     * @return get the position relative multiplied by {@code t}
     */
    private static BlockPos makeLinePos(double t, BlockPos a, BlockPos diff) {
        return new BlockPos((int) (a.getX() + diff.getX() * t), (int) (a.getY() + diff.getY() * t), (int) (a.getZ() + diff.getZ() * t));
    }

    static List<Vec3> makeLine(Vec3 a, Vec3 b, float spacing) {
        Vec3 diff = b.subtract(a);
        int numPoints = (int) (diff.length() / spacing);
        List<Vec3> list = new ArrayList<>();
        MiscHelper.repeat(numPoints, integer -> {
            double t = integer / (numPoints - 1.);
            list.add(a.add(diff.scale(t)));
        });
        return list;
    }

    enum LineSize {
        THIN,
        THICK
    }

    /**
     * moves the given {@code source} vector towards {@code target}
     */
    static Vec3 moveTowards(Vec3 source, Vec3 target, double range, boolean percentage) {
        Vec3 change = source.subtract(target);
        double dist = source.distanceTo(target);
        return percentage ? clampLength(change, dist * range) : clampLength(change, range);
    }

    /**
     * picks a random element from the list
     */
    @Nullable
    static <T> T pickRandom(@NotNull List<T> list) {
        return pickRandom(list, KapLibMod.RANDOM_SOURCE);
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
        return Math.random() <= chance * (living != null ? (1 + living.getAttributeValue(Attributes.LUCK) / 100) : 1);
    }

    /**
     * gets the cooldown time for the given {@link LivingEntity} and the defaultTime
     */
    static int cooldown(LivingEntity living, int defaultTime) {
        return (int) (defaultTime * (1 - living.getAttributeValue(ExtraAttributes.COOLDOWN_REDUCTION.get()) / 100));
    }

    /**
     * gets all entities inside the given AABB source of the given class inside the given level
     */
    static <T extends Entity> List<T> getEntitiesAround(Class<T> tClass, Level level, AABB source, double range) {
        return level.getEntitiesOfClass(tClass, source.inflate(range));
    }

    /**
     * @return the closest entity of the given type, or null if none could be found within the given range
     */
    static <T extends Entity> @Nullable T getClosestEntity(Class<T> tClass, Entity source, double range) {
        List<T> entities = getEntitiesAround(tClass, source, range).stream().filter(t -> t.is(source)).sorted(Comparator.comparingDouble(value -> value.distanceTo(source))).toList();
        if (entities.isEmpty()) return null;
        return entities.get(0);
    }

    /**
     * gets the closest living entity
     * @see MathHelper#getClosestEntity(Class, Entity, double)
     */
    static LivingEntity getClosestLiving(Entity source, double range) {
        return getClosestEntity(LivingEntity.class, source, range);
    }

    /**
     * get Living entities around the given source
     * @see MathHelper#getEntitiesAround(Class, Entity, double) 
     */

    static List<LivingEntity> getLivingAround(Entity source, double range) {
        return getEntitiesAround(LivingEntity.class, source, range);
    }

    /**
     * calculates the view vector of the given x and y rotation
     */
    static Vec3 calculateViewVector(float horizontalHeightXAxis, float verticalYAxis) {
        float f = horizontalHeightXAxis * ((float)Math.PI / 180F);
        float f1 = -verticalYAxis * ((float)Math.PI / 180F);
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
        lineOfSight.stream().collect(CollectorHelper.createMapForKeys(lineOfSight::indexOf))
                .forEach((integer, vec3) -> toReturn.addAll(getEntitiesAround(tClass, level, vec3, incremental * integer).stream().filter(entity -> !toReturn.contains(entity)).toList()));
        return toReturn;
    }

    static <T extends Entity> List<T> getEntitiesAround(Class<T> tClass, Level level, Vec3 loc, double range) {
        return level.getEntitiesOfClass(tClass, new AABB(loc.x - range, loc.y - range, loc.z - range, loc.x + range, loc.y + range, loc.z + range));
    }

    static List<Entity> getAllEntitiesInsideCylinder(float radius, Vec3 sourcePos, Vec2 rot, double range, Level level) {
        List<Entity> toReturn = new ArrayList<>();
        ArrayList<Vec3> lineOfSight = lineOfSight(rot, sourcePos, range, 0.1);
        lineOfSight.forEach(vec3 -> {
            List<Entity> entities = getEntitiesAround(Entity.class, level, vec3, radius);
            toReturn.addAll(entities.stream().filter(entity -> !toReturn.contains(entity)).toList());
        });
        return toReturn;
    }

    static Vec2 createTargetRotation(Entity source, Entity target) {
        return createTargetRotationFromPos(source.position(), target.position());
    }

    static Vec2 createTargetRotationFromPos(@NotNull Vec3 source, @NotNull Vec3 target) {
        double dX = target.x - source.x;
        double dY = target.y - source.y;
        double dZ = target.z - source.z;
        double d3 = Math.sqrt(dX * dX + dZ * dZ);
        return new Vec2(Mth.wrapDegrees((float)(-(Mth.atan2(dY, d3) * (double)(180F / (float)Math.PI)))), Mth.wrapDegrees((float)(Mth.atan2(dZ, dX) * (double)(180F / (float)Math.PI)) - 90.0F));
    }

    static Vec2 createTargetRotationFromEyeHeight(Entity source, Entity target) {
        return createTargetRotationFromPos(source.getEyePosition(), target.getEyePosition());
    }

    static boolean isBehind(Entity source, Entity target) {
        Vec3 vec32 = source.position();
        Vec3 vec31 = vec32.vectorTo(target.position()).normalize();
        vec31 = new Vec3(vec31.x, 0.0D, vec31.z);
        return !(vec31.dot(target.getViewVector(1)) < 0.0D);
    }

    @Contract("null, _ -> fail")
    static Vec3 minimiseLength(Vec3 source, double minimum) {
        if (source.length() > minimum) {
            return source;
        } else {
            double scale = minimum / source.length();
            return source.scale(scale);
        }
    }

    @Contract("null, _ -> fail")
    static Vec3 maximiseLength(Vec3 source, double maximum) {
        if (source.length() < maximum) {
            return source;
        } else {
            double scale = maximum / source.length();
            return source.scale(scale);
        }
    }

    @Contract("null, _ -> fail")
    static Vec3 clampLength(Vec3 source, double value) {
        if (source.length() > value) {
            return maximiseLength(source, value);
        }
        return minimiseLength(source, value);
    }

    static Vec3 getRandomOffsetForPos(Entity target, double dist, double maxOffset) {
        maxOffset *=2;
        RandomSource source = RandomSource.create();
        Vec2 rot = target.getRotationVector();
        Vec3 targetPos = calculateViewVector(rot.x, rot.y).scale(dist);
        Vec3 secPos = removeByScale(calculateViewVector(rot.x - 90, rot.y).scale(maxOffset * source.nextFloat()), 0.5);
        Vec3 thirdPos = removeByScale(calculateViewVector(rot.x, rot.y - 90).scale(maxOffset * source.nextFloat()), 0.5);
        return targetPos.add(secPos).add(thirdPos);
    }

    @Contract("null, _ -> fail")
    static Vec3 removeByScale(Vec3 vec3, double scale) {
        double x = vec3.x;
        double y = vec3.y;
        double z = vec3.z;
        double halfX = (x - (x * scale));
        double halfY = (y - (y * scale));
        double halfZ = (z - (z * scale));
        return new Vec3(halfX, halfY, halfZ);
    }

    static float randomBetween(RandomSource source, float min, float max) {
        return Mth.lerp(source.nextFloat(), min, max);
    }

    static double randomBetween(RandomSource source, double min, double max) {
        return Mth.lerp(source.nextDouble(), min, max);
    }

    static Vec3 randomBetween(RandomSource source, Vec3 min, Vec3 max) {
        return new Vec3(
                randomBetween(source, min.x, max.x),
                randomBetween(source, min.y, max.y),
                randomBetween(source, min.z, max.z)
        );
    }

    static Vec3 randomIn(RandomSource source, AABB box) {
        return new Vec3(
                randomBetween(source, box.minX, box.maxX),
                randomBetween(source, box.minY, box.maxY),
                randomBetween(source, box.minZ, box.maxZ)
        );
    }

    static float getOversizeScale(Vec3 original, Vec3 clamped) {
        return pickLargest((float) (clamped.x / original.x), (float) (clamped.y / original.y), (float) (clamped.z / original.z));
    }

    static float pickLargest(float... values) {
        Float min = null;
        for (float f : values) {
            if (min == null || min < f) {
                min = f;
            }
        }
        return min == null ? -1 : min;
    }
}