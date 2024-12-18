package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {

    public ProjectileMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @ModifyVariable(method = "shootFromRotation", at = @At(value = "HEAD"), index = 4, argsOnly = true)
    private float in(float i, Entity entity, float p_37253_, float p_37254_, float p_37255_, float actualSpeed, float p_37257_) {
        if (entity instanceof LivingEntity living) {
            return (float) (i * (1 + AttributeHelper.getSaveAttributeValue(ExtraAttributes.ARROW_SPEED.get(), living) / 100));
        }
        return i;
    }
}