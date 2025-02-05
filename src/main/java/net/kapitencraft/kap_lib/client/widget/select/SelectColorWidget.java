package net.kapitencraft.kap_lib.client.widget.select;

import net.kapitencraft.kap_lib.client.widget.PositionedWidget;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Predicate;

public class SelectColorWidget extends PositionedWidget {
    private static final int COLOR_SIZE = 5;
    private static final Predicate<String> RGBA_PREDICATE = MathHelper.checkForInteger(0, 255),
            HUE_PREDICATE = MathHelper.checkForInteger(0, 300),
            SV_PREDICATE = MathHelper.checkForInteger(0, 100);

    protected SelectColorWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

    }
}
