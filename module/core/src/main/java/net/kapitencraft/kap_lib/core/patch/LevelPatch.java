package net.kapitencraft.kap_lib.core.patch;

import net.kapitencraft.kap_lib.core.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public interface LevelPatch {

    /**
     * gets a list of any entity of the given class
     * @param tClass type of entities to collect
     * @param span the top angle of the cone
     * @param range the height of the cone
     * @param sourcePos the peak position of the cone
     * @param sourceRot the rotation of the cone with pitch and yaw
     */
    default  <T extends Entity> List<T> getAllEntitiesInsideCone(Class<T> tClass, float span, double range, Vec3 sourcePos, Vec2 sourceRot) {
        double halfSpan = span / 2;
        double incremental = Math.sin(halfSpan) * 0.1;
        List<Vec3> lineOfSight = MathHelper.lineOfSight(sourceRot, sourcePos, range, 0.1);
        List<T> toReturn = new ArrayList<>();
        lineOfSight.stream().collect(CollectorHelper.toMapForKeys(lineOfSight::indexOf))
                .forEach((integer, vec3) -> toReturn.addAll(getEntitiesAround(tClass, vec3, incremental * integer).stream().filter(entity -> !toReturn.contains(entity)).toList()));
        return toReturn;
    }

    /**
     * gets all entities within a cube around the middle point and range
     *
     * @param loc   the middle point of the area
     * @param range the radius of the cube
     */
    default  <T extends Entity> List<T> getEntitiesAround(Class<T> tClass, Vec3 loc, double range) {
        return ((Level) this).getEntitiesOfClass(tClass, new AABB(loc.x - range, loc.y - range, loc.z - range, loc.x + range, loc.y + range, loc.z + range));
    }

    /**
     * gets all entities inside the given AABB source of the given class inside the given level
     */
    default <T extends Entity> List<T> getEntitiesAround(Class<T> tClass, AABB source, double range) {
        return ((Level) this).getEntitiesOfClass(tClass, source.inflate(range));
    }

    /**
     * gets all entities within a cylinder
     *
     * @param radius    the radius of the cylinder
     * @param sourcePos the bottom center of the cylinder
     * @param rot       the rotation of the cylinder
     * @param height    the height of the cylinder
     */
    default List<Entity> getAllEntitiesInsideCylinder(float radius, Vec3 sourcePos, Vec2 rot, double height) {
        List<Entity> toReturn = new ArrayList<>();
        ArrayList<Vec3> lineOfSight = MathHelper.lineOfSight(rot, sourcePos, height, 0.1);
        lineOfSight.forEach(vec3 -> {
            List<Entity> entities = getEntitiesAround(Entity.class, vec3, radius);
            toReturn.addAll(entities.stream().filter(entity -> !toReturn.contains(entity)).toList());
        });
        return toReturn;
    }

    /**
     * @param key the key of the damage type
     * @return the Holder of the damage type
     * @throws IllegalStateException if the key doesn't exist
     */
    default Holder<DamageType> lookupDamageTypeHolder(ResourceKey<DamageType> key) {
        return ((Level) this).registryAccess().holderOrThrow(key);
    }

    /**
     * modifies a specific property of the block at the given position in the given level
     * @param pos the position to update on
     * @param property the property to change
     * @param value the new value to change to
     * @param flags the change flags
     */
    default <C extends Comparable<C>> void updateState(BlockPos pos, Property<C> property, C value, int flags) {
        Level level = (Level) this;
        level.setBlock(pos, level.getBlockState(pos).setValue(property, value), flags);
    }

    default  <T extends ParticleOptions> int sendParticles(T type, boolean force, double x, double y, double z, int amount, double deltaX, double deltaY, double deltaZ, double speed) {
        if (this instanceof ServerLevel serverLevel) {
            List<ServerPlayer> players = serverLevel.getPlayers(serverPlayer -> true);
            int i;
            for (i = 0; i < players.size(); i++) {
                ServerPlayer serverPlayer = players.get(i);
                serverLevel.sendParticles(serverPlayer, type, force, x, y, z, amount, deltaX, deltaY, deltaZ, speed);
            }
            return i;
        }
        return -1;
    }

    default  <T extends ParticleOptions> int sendParticles(T type, boolean force, Vec3 loc, int amount, double deltaX, double deltaY, double deltaZ, double speed) {
        return sendParticles(type, force, loc.x, loc.y, loc.z, amount, deltaX, deltaY, deltaZ, speed);
    }

    default void sendAlwaysVisibleParticles(ParticleOptions type, double x, double y, double z, double dx, double dy, double dz, double sx, double sy, double sz, int amount) {
        Level level = (Level) this;
        for (int i = 0; i < amount; i++) {
            double xOff = (Math.random() * 2 * dx) - dx;
            double yOff = (Math.random() * 2 * dy) - dy;
            double zOff = (Math.random() * 2 * dz) - dz;
            level.addAlwaysVisibleParticle(type, true, x + xOff, y + yOff, z + zOff, sx, sy, sz);
        }
    }
}
