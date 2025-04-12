package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.event.custom.LivingStartGlidingEvent;
import net.kapitencraft.kap_lib.item.combat.LibSwordItem;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.kapitencraft.kap_lib.requirements.RequirementType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements IForgePlayer {

    @Shadow public abstract void increaseScore(int pScore);

    @Shadow public abstract void remove(Entity.RemovalReason pReason);

    @Shadow public abstract void playNotifySound(SoundEvent pSound, SoundSource pSource, float pVolume, float pPitch);

    @Shadow public abstract void playSound(SoundEvent pSound, float pVolume, float pPitch);

    private Player self() {
        return (Player) (Object) this;
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    public void checkGlideAllowed(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = self().getItemBySlot(EquipmentSlot.CHEST);
        if (!RequirementManager.instance.meetsRequirements(RequirementType.ITEM, stack.getItem(), self())) {
            cir.setReturnValue(false);
        }
        LivingStartGlidingEvent event = new LivingStartGlidingEvent(self(), stack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) cir.setReturnValue(false);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;playerAttack(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/damagesource/DamageSource;"))
    public DamageSource extendDamageSource(DamageSources instance, Player pPlayer) {
        ItemStack sword = pPlayer.getMainHandItem();
        if (sword.getItem() instanceof LibSwordItem libSwordItem) {
            return instance.source(libSwordItem.getType(), pPlayer);
        }
        return instance.playerAttack(pPlayer);
    }
}
