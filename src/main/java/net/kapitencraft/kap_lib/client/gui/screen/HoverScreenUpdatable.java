package net.kapitencraft.kap_lib.client.gui.screen;

import net.kapitencraft.kap_lib.client.gui.BlockEntityMenu;
import net.kapitencraft.kap_lib.client.gui.screen.tooltip.HoverTooltip;

import java.util.ArrayList;

public abstract class HoverScreenUpdatable<T extends BlockEntityMenu<?>> extends HoverTooltip {
    protected final T menu;
    public HoverScreenUpdatable(int xOffsetStart, int yOffsetStart, int xSize, int ySize, T menu) {
        super(xOffsetStart, yOffsetStart, xSize, ySize, new ArrayList<>());
        this.menu = menu;
    }

    /**
     * change the text of this tooltip
     */
    public abstract void tick();

    /**
     * @return if this HoverTooltip has been changed (use menu instance)
     * if it returns true, the {@link HoverScreenUpdatable#tick() tick} method will be called to make changes to this tooltip (changing its text)
     */
    public abstract boolean changed();
}
