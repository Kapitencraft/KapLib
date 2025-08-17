package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.entity.fishing.IFishingHook;
import net.kapitencraft.kap_lib.entity.fishing.AbstractFishingHook;
import net.kapitencraft.kap_lib.event.custom.ModifyFishingHookStatsEvent;
import net.kapitencraft.kap_lib.item.tools.fishing.ModFishingRod;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.kapitencraft.kap_lib.requirements.type.RegistryReqType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingRodItem.class)
public abstract class FishingRodMixin extends Item {

    FishingRodItem self() {
        return (FishingRodItem) (Object) this;
    }

    public FishingRodMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public boolean spawnHook(Level level, Entity entity, Level ignored, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!RequirementManager.instance.meetsRequirements(RegistryReqType.ITEM, stack.getItem(), player)) {
            return false;
        }

        int lureSpeed = EnchantmentHelper.getFishingSpeedBonus(stack);
        int luckBonus = EnchantmentHelper.getFishingLuckBonus(stack);
        ModifyFishingHookStatsEvent event = new ModifyFishingHookStatsEvent(entity, player, lureSpeed, luckBonus, stack);
        MinecraftForge.EVENT_BUS.post(event);
        lureSpeed = event.lureSpeed.calculate();
        luckBonus = event.luck.calculate();
        int hookSpeed = event.hookSpeed.calculate();
        if (self() instanceof ModFishingRod fishingRod) {
            AbstractFishingHook hook = fishingRod.create(player, level, lureSpeed, luckBonus);
            hook.setHookSpeedModifier(hookSpeed);
            return level.addFreshEntity(hook);
        }
        FishingHook hook = (FishingHook) entity;
        hook.lureSpeed = lureSpeed;
        hook.luck = luckBonus;
        ((IFishingHook) hook).setHookSpeedModifier(hookSpeed);
        return level.addFreshEntity(hook);
    }
}
