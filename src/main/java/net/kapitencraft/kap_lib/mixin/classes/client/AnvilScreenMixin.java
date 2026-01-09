package net.kapitencraft.kap_lib.mixin.classes.client;

import net.kapitencraft.kap_lib.core.config.ServerModConfig;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {

    public AnvilScreenMixin(AnvilMenu pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation pMenuResource) {
        super(pMenu, pPlayerInventory, pTitle, pMenuResource);
    }

    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 40))
    private int getAnvilCap(int constant) {
        return ServerModConfig.getAnvilLimit();
    }
}
