package net.kapitencraft.kap_lib.item.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.item.combat.LibSwordItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {

    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;playerAttack(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/damagesource/DamageSource;"))
    public DamageSource extendDamageSource(DamageSources instance, Player player, Operation<DamageSource> original) {
        ItemStack sword = player.getMainHandItem();
        if (sword.getItem() instanceof LibSwordItem libSwordItem) {
            return instance.source(libSwordItem.getDamageType(), player);
        }
        return original.call(instance, player);
    }
}
