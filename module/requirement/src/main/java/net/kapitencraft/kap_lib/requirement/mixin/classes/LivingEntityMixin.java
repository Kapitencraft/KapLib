package net.kapitencraft.kap_lib.requirement.mixin.classes;

import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.kapitencraft.kap_lib.requirement.type.RegistryReqType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntityExtension {

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot pSlot);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updateFallFlying", at = @At(value = "HEAD"), cancellable = true)
    private void checkRequirements(CallbackInfo ci) {
        if (!level().isClientSide() && !RequirementManager.instance.meetsRequirements(RegistryReqType.ITEM, getItemBySlot(EquipmentSlot.CHEST).getItem(), self())) {
            setSharedFlag(7, false);
            ci.cancel();
        }
    }

}
