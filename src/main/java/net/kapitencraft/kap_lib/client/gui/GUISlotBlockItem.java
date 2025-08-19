package net.kapitencraft.kap_lib.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * add to custom Container slot to effectively block it (probably easier way to do this)
 */
public class GUISlotBlockItem extends Item {
    private List<Component> tooltip;

    public GUISlotBlockItem() {
        super(new Properties().stacksTo(1));
    }

    public GUISlotBlockItem putTooltip(List<Component> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> list, TooltipFlag p_41424_) {
        list.addAll(this.tooltip);
    }
}
