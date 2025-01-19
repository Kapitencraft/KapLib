package net.kapitencraft.kap_lib.client.overlay.holder;

import net.kapitencraft.kap_lib.client.overlay.OverlayManager;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.overlay.box.ResizeBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;

/**
 * an overlay element. attach it to the OverlayController
 */
public abstract class Overlay {
    private final OverlayProperties properties;
    private final Component name;

    public Overlay(OverlayProperties holder, Component name) {
        this.properties = holder;
        this.name = name;
    }

    /**
     * @return the top-left position of this overlay
     */
    public Vec2 getLoc(float screenWidth, float screenHeight) {
        return properties.getLoc(screenWidth, screenHeight);
    }

    public void move(Vec2 toAdd) {
        this.properties.add(toAdd);
    }

    public void moveX(float offset) {
        this.properties.addX(offset);
    }

    public void moveY(float offset) {
        this.properties.addY(offset);
    }


    /**
     * renders the Component relative to this.pos.x and this.pos.y + y
     */
    protected void renderString(GuiGraphics graphics, Component toWrite, float y) {
        graphics.drawString(Minecraft.getInstance().font, toWrite, 0, (int) y, -1);
    }

    /**
     * creates a new {@link ResizeBox} for this Overlay
     */
    public ResizeBox newBox(float screenWidth, float screenHeight, LocalPlayer player, Font font) {
        Vec2 loc = this.getLoc(screenWidth, screenHeight);
        float width = this.getWidth(player, font) * this.properties.getXScale();
        float height = this.getHeight(player, font) * this.properties.getYScale();
        return new ResizeBox(loc.add(new Vec2(-1, -2)), loc.add(new Vec2(width + 1, height)), this);
    }

    public void reset(float screenWidth, float screenHeight, LocalPlayer player, Font font, ResizeBox resizeBox) {
        Vec2 loc = this.getLoc(screenWidth, screenHeight);
        float width = this.getWidth(player, font) * this.properties.getXScale();
        float height = this.getHeight(player, font) * this.properties.getYScale();
        resizeBox.start = loc.add(new Vec2(-1, -2));
        resizeBox.end = loc.add(new Vec2(width + 1, height));
    }


    public abstract float getWidth(LocalPlayer player, Font font);
    public abstract float getHeight(LocalPlayer player, Font font);


    /**
     * render this overlay
     */
    public abstract void render(GuiGraphics graphics, float posX, float posY, LocalPlayer player);

    /**
     * @return the PositionHolder this object contains
     */
    public OverlayProperties getProperties() {
        return properties;
    }

    public boolean isVisible() {
        return properties.isVisible();
    }

    public void setVisible(boolean b) {
        OverlayManager.setVisible(this, b);
        this.properties.setVisible(b);
    }

    public Component getName() {
        return this.name;
    }
}
