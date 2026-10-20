package net.kapitencraft.kap_lib.core.mixin.classes;

import net.kapitencraft.kap_lib.core.patch.EntityPatch;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements EntityPatch {
}
