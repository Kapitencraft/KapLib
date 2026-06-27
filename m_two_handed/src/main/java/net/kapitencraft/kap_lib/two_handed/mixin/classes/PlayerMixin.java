package net.kapitencraft.kap_lib.two_handed.mixin.classes;

import net.kapitencraft.kap_lib.two_handed.mixin.duck.OffhandAttackCooldownHolder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements OffhandAttackCooldownHolder {
    //dummy constructor
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract float getCurrentItemAttackStrengthDelay();

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot1);

    @Unique
    private int offhandAttackStrengthTicker;
    @Unique
    private ItemStack lastItemInOffhand = ItemStack.EMPTY;

    @Override
    public float getOffhandAttackStrengthScale(float adjustTicks) {
        return Mth.clamp(((float)this.offhandAttackStrengthTicker + adjustTicks) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
    }

    @Override
    public void resetOffhandAttackStrengthTicker() {
        this.offhandAttackStrengthTicker = 0;
    }

    @Inject(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/world/entity/player/Player;attackStrengthTicker:I"))
    private void tickOffhandAttackStrengthTicker(CallbackInfo ci) {
        this.offhandAttackStrengthTicker++;
        ItemStack stack = this.getOffhandItem();
        if (!ItemStack.matches(this.lastItemInOffhand, stack)) {
            if (!ItemStack.isSameItem(this.lastItemInOffhand, stack)) {
                this.resetOffhandAttackStrengthTicker();
            }

            this.lastItemInOffhand = stack.copy();
        }
    }
}
