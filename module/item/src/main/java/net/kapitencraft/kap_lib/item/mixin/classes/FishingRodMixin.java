package net.kapitencraft.kap_lib.item.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
import net.kapitencraft.kap_lib.item.entity.fishing.AbstractFishingHook;
import net.kapitencraft.kap_lib.item.entity.fishing.IFishingHook;
import net.kapitencraft.kap_lib.item.event.custom.ModifyFishingHookStatsEvent;
import net.kapitencraft.kap_lib.item.tools.fishing.ModFishingRod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FishingRodItem.class)
public class FishingRodMixin extends Item implements MixinSelfProvider<FishingRodItem> {

    public FishingRodMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public boolean spawnHook(Level level, Entity entity, Operation<Boolean> original, @Local(argsOnly = true) Player player, @Local(argsOnly = true) InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FishingHook hook = (FishingHook) entity;
        int lureSpeed = hook.lureSpeed;
        int luckBonus = hook.luck;
        ModifyFishingHookStatsEvent event = new ModifyFishingHookStatsEvent(entity, player, lureSpeed, luckBonus, stack);
        NeoForge.EVENT_BUS.post(event);
        lureSpeed = event.lureSpeed.calculate();
        luckBonus = event.luck.calculate();
        int hookSpeed = event.hookSpeed.calculate();
        if (self() instanceof ModFishingRod fishingRod) {
            AbstractFishingHook modHook = fishingRod.create(player, level, lureSpeed, luckBonus);
            modHook.setHookSpeedModifier(hookSpeed);
            return original.call(level, modHook);
        }
        hook.lureSpeed = lureSpeed;
        hook.luck = luckBonus;
        ((IFishingHook) hook).setHookSpeedModifier(hookSpeed);
        return original.call(level, hook);
    }
}
