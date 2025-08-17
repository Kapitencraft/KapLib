package net.kapitencraft.kap_lib.client.gui;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * add to custom Container slot to effectively block it (probably easier way to do this)
 */
public class GUISlotBlockItem extends Item {

    public GUISlotBlockItem(List<Component> tooltip) {
        super(new Properties().stacksTo(1).component(DataComponents.LORE, new ItemLore(tooltip)));
    }
}
