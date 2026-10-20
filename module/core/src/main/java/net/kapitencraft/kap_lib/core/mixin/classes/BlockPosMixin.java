package net.kapitencraft.kap_lib.core.mixin.classes;

import net.kapitencraft.kap_lib.core.patch.BlockPosPatch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockPos.class)
public class BlockPosMixin extends Vec3i implements BlockPosPatch {

    public BlockPosMixin(int x, int y, int z) {
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
