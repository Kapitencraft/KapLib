package net.kapitencraft.kap_lib.client.overlay.box;

import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;

public class RenderBox {
    /**
     * the top-left position of this RenderBox relative to the top left of the screen
     */
    public Vec2 start;
    /**
     * the bottom-right position of this RenderBox relative to the top left of the screen
     */
    public Vec2 end;
    /**
     * the cursorType which should be swapped to when hovered
     * @see org.lwjgl.glfw.GLFW#GLFW_ARROW_CURSOR Arrow Cursor and others (below)
     */
    private final int cursorType;
    /**
     * the color this box should be rendered in
     */
    private final int color;

    public RenderBox(Vec2 start, Vec2 end, int cursorType, int color) {
        this.start = start;
        this.end = end;
        this.cursorType = cursorType;
        this.color = color;
    }

    /**
     * @return the width of this Box
     */
    protected float width() {
        return Math.abs(this.end.x - this.start.x);
    }

    /**
     * @return the height of this box
     */
    protected float height() {
        return Math.abs(this.end.y - this.start.y);
    }

    /**
     * @param mouseX the mouse X position
     * @param mouseY the mouse Y position
     * @return the id of the Cursor type that should be rendered
     */
    public int getCursorType(double mouseX, double mouseY) {
        return cursorType;
    }

    /**
     * moves this box around (change X and Y coordinates)
     * @param toAdd the offset applied to this Box
     */
    public void move(Vec2 toAdd) {
        this.start = this.start.add(toAdd);
        this.end = this.end.add(toAdd);
    }

    /**
     * render this Box
     */
    public void render(GuiGraphics graphics, double mouseX, double mouseY) {
        ClientHelper.fill(graphics, start.x, start.y, end.x, end.y, color, -1);
    }

    public void moveX(float offset) {
        this.start = new Vec2(this.start.x + offset, this.start.y);
    }

    public void moveY(float offset) {
        this.start = new Vec2(this.start.x, this.start.y + offset);
    }
}
