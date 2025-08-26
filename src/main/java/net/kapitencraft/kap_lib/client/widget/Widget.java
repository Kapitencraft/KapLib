package net.kapitencraft.kap_lib.client.widget;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.NotNull;

public abstract class Widget implements Renderable, GuiEventListener, NarratableEntry {

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public void setFocused(boolean pFocused) {
    }
}
