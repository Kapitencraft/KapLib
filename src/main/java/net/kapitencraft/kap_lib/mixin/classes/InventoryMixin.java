package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow
    @Final
    public NonNullList<ItemStack> items;

    @Shadow @Final public Player player;

    @Shadow public int selected;

    /**
     * @reason Mining Speed Attribute
     * @author Kapitencraft
     */
    @Overwrite
    public float getDestroySpeed(BlockState state) {
        return items.get(selected).getDestroySpeed(state) == 1 ? 1f : (float) player.getAttributeValue(ExtraAttributes.MINING_SPEED.get());
    }
}
