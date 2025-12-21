package net.kapitencraft.kap_lib.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.core.event.custom.LivingStartGlidingEvent;
import net.kapitencraft.kap_lib.item.combat.LibSwordItem;
import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.kapitencraft.kap_lib.requirement.type.RegistryReqType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerExtension, MixinSelfProvider<Player> {

    @Shadow public abstract void remove(Entity.RemovalReason pReason);

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    public void checkGlideAllowed(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = self().getItemBySlot(EquipmentSlot.CHEST);
        if (!RequirementManager.instance.meetsRequirements(RegistryReqType.ITEM, stack.getItem(), self())) {
            cir.setReturnValue(false);
        }
        LivingStartGlidingEvent event = new LivingStartGlidingEvent(self(), stack);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) cir.setReturnValue(false);
    }

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;playerAttack(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/damagesource/DamageSource;"))
    public DamageSource extendDamageSource(DamageSources instance, Player player, Operation<DamageSource> original) {
        ItemStack sword = player.getMainHandItem();
        if (sword.getItem() instanceof LibSwordItem libSwordItem) {
            return instance.source(libSwordItem.getDamageType(), player);
        }
        return original.call(instance, player);
    }

    @Override
    public Player self() {
        return MixinSelfProvider.super.self();
    }
}
