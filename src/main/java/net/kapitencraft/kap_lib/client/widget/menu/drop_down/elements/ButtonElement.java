package net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements;

import net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ButtonElement extends Element {
    private final Runnable runnable;

    public ButtonElement(@Nullable ListElement parent, DropDownMenu menu, Component name, Runnable runnable) {
        super(parent, menu, name);
        this.runnable = runnable;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void click(float relativeX, float relativeY) {
        this.runnable.run();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
    }

    public static class Builder extends Element.Builder<ButtonElement, Builder> {
        private Runnable executor;

        public Builder setExecutor(Runnable executor) {
            this.executor = executor;
            return this;
        }

        @Override
        public ButtonElement build(ListElement element, DropDownMenu menu) {
            Validate.notNull(executor, "executor may not be null");
            return new ButtonElement(element, menu, name, executor);
        }
    }
}
