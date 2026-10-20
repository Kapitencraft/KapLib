package net.kapitencraft.kap_lib.two_handed.mixin.classes;

import net.kapitencraft.kap_lib.two_handed.TwoHandedModule;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.inventory.InventoryMenu$1")
public class InventoryMenuOffhandSlotWrapperMixin extends Slot {

    @Shadow
    @Final
    Player val$owner;

    public InventoryMenuOffhandSlotWrapperMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean isActive() {
        return !TwoHandedModule.isTwoHanded(val$owner.getMainHandItem(), val$owner) && super.isActive();
    }
}
