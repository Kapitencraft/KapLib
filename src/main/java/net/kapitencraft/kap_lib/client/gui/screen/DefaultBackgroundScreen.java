package net.kapitencraft.kap_lib.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DefaultBackgroundScreen extends Screen implements IBackgroundScreen {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("kap_lib:textures/gui/background.png");

    /**
     * the position of the background texture
     */
    protected int leftPos;
    protected int topPos;


    protected DefaultBackgroundScreen(Component pTitle) {
        super(pTitle);
    }

    @Override
    protected void init() {
        this.leftPos = this.leftPos(this.width);
        this.topPos = this.topPos(this.height);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {
        graphics.blit(BACKGROUND_TEXTURE, this.leftPos, this.topPos, 0, 0, 0, getImageWidth(),  getImageHeight(), getImageWidth(), getImageHeight());
    }

    @Override
    public int getImageWidth() {
        return 219;
    }

    @Override
    public int getImageHeight() {
        return 180;
    }
}
