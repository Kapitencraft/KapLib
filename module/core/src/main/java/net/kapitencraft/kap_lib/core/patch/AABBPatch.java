package net.kapitencraft.kap_lib.core.patch;

import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface AABBPatch {

    default Vec3 randomIn(RandomSource source) {
        AABB box = (AABB) this;
        return new Vec3(
                MathHelper.randomBetween(source, box.minX, box.maxX),
                MathHelper.randomBetween(source, box.minY, box.maxY),
                MathHelper.randomBetween(source, box.minZ, box.maxZ)
        );
    }
}
