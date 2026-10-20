package net.kapitencraft.kap_lib.core.mixin.classes;

import net.kapitencraft.kap_lib.core.patch.AABBPatch;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AABB.class)
public class AABBMixin implements AABBPatch {
}
