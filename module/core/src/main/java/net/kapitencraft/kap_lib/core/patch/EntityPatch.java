package net.kapitencraft.kap_lib.core.patch;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public interface EntityPatch {

    default List<LivingEntity> getLivingAround(double range) {
        return getEntitiesAround(LivingEntity.class, range);
    }

    /**
     * @return a list of entities that surround the given source in a {@code range} radius and is an instance of {@code tClass}
     */
    default <T extends Entity> List<T> getEntitiesAround(Class<T> tClass, double range) {
        Entity source = (Entity) this;
        return source.level().getEntitiesAround(tClass, source.getBoundingBox(), range);
    }

    /**
     * @return the closest entity of the given type, or null if none could be found within the given range
     */
    default <T extends Entity> @Nullable T getClosestEntity(Class<T> tClass, double range) {
        return getClosestEntity(tClass, range, v -> true);
    }

    /**
     * @param type the type of entity to search for
     * @param range the maximum distance to check in
     * @param filter filter for which entities are eligible
     * @return the closest entity, or null, if no eligible entity was found
     */
    default <T extends Entity> @Nullable T getClosestEntity(Class<T> type, double range, Predicate<T> filter) {
        Entity origin = (Entity) this;
        List<T> entities = getEntitiesAround(type, range).stream().filter(t -> !t.is(origin) && filter.test(t)).toList();
        if (entities.isEmpty()) return null;
        T val = entities.getFirst();
        for (int i = 1; i < entities.size(); i++) {
            if (val.distanceTo(origin) > entities.get(i).distanceTo(origin)) {
                val = entities.get(i);
            }
        }
        return val;
    }

    /**
     * gets the closest living entity
     *
     * @see getClosestEntity(Class, double)
     */
    default LivingEntity getClosestLiving(double range) {
        return getClosestEntity(LivingEntity.class, range);
    }

    /**
     * checks whether the given source entity is behind the given target entity
     */
    default boolean isBehind(Entity target) {
        Vec3 vec32 = ((Entity) this).position();
        Vec3 vec31 = vec32.vectorTo(target.position()).normalize();
        vec31 = new Vec3(vec31.x, 0.0D, vec31.z);
        return !(vec31.dot(target.getViewVector(1)) < 0.0D);
    }

    /**
     * @return a list of locations in the line of sight ot the given entity
     */
    default ArrayList<Vec3> lineOfSight(double range, double scaling) {
        Entity entity = (Entity) this;
        Vec3 viewVec = entity.calculateViewVector(entity.getXRot(), entity.getYRot());
        Vec3 viewVecWithLoc = viewVec.add(entity.getEyePosition());
        Vec3 end = viewVec.scale(range).add(entity.getEyePosition());
        BlockHitResult result = entity.level().clip(new ClipContext(viewVecWithLoc, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        Vec3 diff = result.getLocation().subtract(viewVecWithLoc);
        ArrayList<Vec3> list = new ArrayList<>();
        for (int i = 0; i < diff.length() / scaling; i++) {
            list.add(MathHelper.clampLength(diff, i * scaling).add(entity.getEyePosition()));
        }
        return list;
    }

    /**
     * merge the CompoundTag into the given entity
     */
    @SuppressWarnings("ALL")
    public default void injectCompoundTag(CompoundTag tag) {
        CompoundTag data = ((Entity) this).getPersistentData();
        Set<String> allKeys = tag.getAllKeys();
        allKeys.forEach(s -> {
            if (tag.get(s) != null) {
                if (tag.get(s) instanceof CompoundTag cTag)
                    injectCompoundTag(cTag);
                else data.put(s, tag.get(s));
            }
        });
    }

    /**
     * method to teleport entity maxRange blocks forward, checking block hits
     *
     * @param maxRange maximal range of the teleport, reduced when hitting a block
     * @return if the entity has been teleported
     */
    default boolean saveTeleport(double maxRange) {
        try {
            Entity entity = (Entity) this;
            Vec3 targetPos = entity.getLookAngle().scale(maxRange);
            entity.stopRiding();
            entity.move(MoverType.SELF, targetPos);
        } catch (Exception e) {
            LibConstants.LOGGER.warn("error trying to teleport entity '{}': {}", this, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * method to directly teleport a player to the target location
     * sending sounds and resetting fall-distance
     *
     * @param teleportPosition target teleportation location
     */
    default void teleport(Vec3 teleportPosition) {
        Entity entity = (Entity) this;
        entity.teleportTo(teleportPosition.x, teleportPosition.y, teleportPosition.z);
        entity.fallDistance = 0;
        entity.level().playSound(entity, BlockPos.containing(entity.position()), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
    }

    default  <T extends ParticleOptions> int sendParticles(T type, boolean force, int amount, double deltaX, double deltaY, double deltaZ, double speed) {
        Entity source = (Entity) this;
        return source.level().sendParticles(type, force, source.position(), amount, deltaX, deltaY, deltaZ, speed);
    }

}
