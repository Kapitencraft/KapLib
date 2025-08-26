package net.kapitencraft.kap_lib.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * a "go back" button
 */
public class BackButton extends Button {

    private final boolean left;
    private final Screen target;

    protected BackButton(int pX, int pY, int pSize, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration, boolean left, Screen target) {
        super(pX, pY, pSize, pSize, pMessage, pOnPress, pCreateNarration);
        this.left = left;
        this.target = target;
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return Math.abs(pMouseY - this.getY()) <= 10 && Math.abs(pMouseX - this.getX()) >= this.getWidth() - 10;
    }


    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        float scale = 8f / this.height;
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 0);
        graphics.drawCenteredString(Minecraft.getInstance().font, this.left ? "<" : ">", this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, -1);
        graphics.pose().popPose();
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        playDownSound(Minecraft.getInstance().getSoundManager());
        Minecraft.getInstance().setScreen(this.target);
    }
}
