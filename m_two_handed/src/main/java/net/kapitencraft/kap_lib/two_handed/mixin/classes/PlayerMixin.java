package net.kapitencraft.kap_lib.two_handed.mixin.classes;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.two_handed.mixin.duck.OffhandAttackCooldownHolder;
import net.kapitencraft.kap_lib.two_handed.mixin.duck.TwoHandedSuppressor;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements OffhandAttackCooldownHolder, TwoHandedSuppressor {
    //dummy constructor
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract float getCurrentItemAttackStrengthDelay();

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot1);

    @Shadow public abstract float getAttackStrengthScale(float adjustTicks);

    @Shadow @Nonnull public abstract ItemStack getWeaponItem();

    //region OffhandAttackCooldownHolder
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

    @Override
    public boolean shouldAttackOffhand() {
        return getAttackStrengthScale(0) != 1 && //priorities main hand item
                offhandAttackStrengthTicker > attackStrengthTicker;
    }

    @Override
    public float getUsedAttackStrengthScale(float adjustTicks) {
        return shouldAttackOffhand() ? getOffhandAttackStrengthScale(adjustTicks) : getAttackStrengthScale(adjustTicks);
    }

    @Override
    public void swapToOffhandAttributes() {
        AttributeMap map = this.getAttributes();
        ItemAttributeModifiers mainhandModifiers = getMainHandItem().getAttributeModifiers();
        ItemAttributeModifiers offhandModifiers = getOffhandItem().getAttributeModifiers();
        BiConsumer<Holder<Attribute>, AttributeModifier> removeAction = (attribute, modifier) -> {
            AttributeInstance attributeinstance = map.getInstance(attribute);
            if (attributeinstance != null) {
                attributeinstance.removeModifier(modifier);
            }
        };
        BiConsumer<Holder<Attribute>, AttributeModifier> addAction = (p_352705_, p_352706_) -> {
            AttributeInstance attributeinstance = map.getInstance(p_352705_);
            if (attributeinstance != null) {
                attributeinstance.removeModifier(p_352706_.id());
                attributeinstance.addTransientModifier(p_352706_);
            }
        };

        //disable mainhand modifiers for mainhand item
        mainhandModifiers.forEach(EquipmentSlot.MAINHAND, removeAction);
        //disable offhand modifiers for offhand item
        offhandModifiers.forEach(EquipmentSlot.OFFHAND, removeAction);
        //enable mainhand modifiers for offhand item
        offhandModifiers.forEach(EquipmentSlot.MAINHAND, addAction);
        //enable offhand modifiers for mainhand item
        mainhandModifiers.forEach(EquipmentSlot.OFFHAND, addAction);
    }

    @Override
    public void swapToMainhandAttributes() {
        AttributeMap map = this.getAttributes();
        ItemAttributeModifiers mainhandModifiers = getMainHandItem().getAttributeModifiers();
        ItemAttributeModifiers offhandModifiers = getOffhandItem().getAttributeModifiers();
        BiConsumer<Holder<Attribute>, AttributeModifier> removeAction = (attribute, modifier) -> {
            AttributeInstance attributeinstance = map.getInstance(attribute);
            if (attributeinstance != null) {
                attributeinstance.removeModifier(modifier);
            }
        };
        BiConsumer<Holder<Attribute>, AttributeModifier> addAction = (p_352705_, p_352706_) -> {
            AttributeInstance attributeinstance = map.getInstance(p_352705_);
            if (attributeinstance != null) {
                attributeinstance.removeModifier(p_352706_.id());
                attributeinstance.addTransientModifier(p_352706_);
            }
        };

        //disable mainhand modifiers for offhand item
        offhandModifiers.forEach(EquipmentSlot.MAINHAND, removeAction);
        //disable offhand modifiers for mainhand item
        mainhandModifiers.forEach(EquipmentSlot.OFFHAND, removeAction);
        //enable mainhand modifiers for mainhand item
        mainhandModifiers.forEach(EquipmentSlot.MAINHAND, addAction);
        //enable offhand modifiers for offhand item
        offhandModifiers.forEach(EquipmentSlot.OFFHAND, addAction);
    }
    //endregion

    //region TwoHandedSuppressor
    @Unique
    boolean suppressesTwoHanded;

    @Override
    public boolean suppressesTwoHanded() {
        return suppressesTwoHanded;
    }

    @Override
    public void setSuppressed(boolean suppressed) {
        this.suppressesTwoHanded = suppressed;
    }
    //endregion

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

    @ModifyReturnValue(method = "getWeaponItem", at = @At("RETURN"))
    private ItemStack includeOffhand(ItemStack original) {
        if (isAutoSpinAttack() || !shouldAttackOffhand())
            return original;
        return getOffhandItem();
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttackStrengthScale(F)F"))
    private float changeUsedAttackStrengthScale(Player instance, float adjustTicks, Operation<Float> original) {
        return instance.getUsedAttackStrengthScale(adjustTicks);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack changeToWeapon(Player instance, InteractionHand hand, Operation<ItemStack> original) {
        return getWeaponItem();
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isAutoSpinAttack()Z"))
    private void swapAttributesIfNecessary(Entity target, CallbackInfo ci) {
        if (shouldAttackOffhand())
            swapToOffhandAttributes();
    }

    @Inject(method = "attack", at = @At("RETURN"))
    private void swapBackAttributesIfNecessary(Entity target, CallbackInfo ci) {
        if (shouldAttackOffhand())
            swapToMainhandAttributes();
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"))
    private void includeOffhandReset(Player instance, Operation<Void> original) {
        //remove reset in order to swap attributes back correctly
    }
}
