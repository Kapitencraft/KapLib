package net.kapitencraft.kap_lib.two_handed.mixin.classes;

import net.kapitencraft.kap_lib.two_handed.TwoHandedModule;
import net.kapitencraft.kap_lib.two_handed.mixin.duck.TwoHandedSuppressor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements TwoHandedSuppressor {

    //region TwoHandedSuppressor
    @Unique
    Set<ResourceLocation> flags = new HashSet<>();

    @Override
    public boolean kap_lib$suppressesTwoHanded() {
        return !flags.isEmpty();
    }

    @Override
    public void kap_lib$addSuppressionFlag(ResourceLocation location) {
        if (flags.isEmpty() &&
                (Object) this instanceof Player player &&
                TwoHandedModule.isItemTwoHanded(player.getMainHandItem())
        ) {
            player.getInventory().clearOffhand();
        }
        flags.add(location);
    }

    @Override
    public void kap_lib$removeSuppressionFlag(ResourceLocation location) {
        flags.remove(location);
    }

    @Override
    public boolean kap_lib$hasSuppressionFlag(ResourceLocation key) {
        return flags.contains(key);
    }
    //endregion
}
