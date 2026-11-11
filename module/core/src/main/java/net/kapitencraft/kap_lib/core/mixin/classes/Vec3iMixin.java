package net.kapitencraft.kap_lib.core.mixin.classes;

import net.kapitencraft.kap_lib.core.patch.Vec3iPatch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockPos.class)
public class Vec3iMixin extends Vec3i implements Vec3iPatch {

    public Vec3iMixin(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public BlockPos scale(double scale) {
        return new BlockPos(
                (int) (getX() * scale),
                (int) (getY() * scale),
                (int) (getZ() * scale)
        );
    }
}
