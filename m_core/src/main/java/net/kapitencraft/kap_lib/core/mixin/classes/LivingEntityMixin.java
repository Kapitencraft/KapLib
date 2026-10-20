package net.kapitencraft.kap_lib.core.mixin.classes;

import net.kapitencraft.kap_lib.core.patch.LivingEntityPatch;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityPatch {

}
