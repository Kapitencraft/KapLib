package net.kapitencraft.kap_lib.client.overlay.box;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

/**
 * boxes that can be rendered on screen and interacted with
 * <br> (hence the {@link GuiEventListener} implementation)
 */
public class InteractiveBox extends RenderBox implements GuiEventListener {
    protected InteractiveBox(Vec2 start, Vec2 finish, int cursorType, int color) {
        super(start, finish, cursorType, color);
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        return check(start.x, end.x, x) && check(start.y, end.y, y);
    }

    private static boolean check(float s, float f, double v) {
        return Mth.clamp(v, s, f) == v;
    }

    @Override
    public final boolean mouseDragged(double newX, double newY, int clickType, double changeX, double changeY) {
        double oldX = -changeX + newX;
        double oldY = -changeY + newY;
        return this.mouseDrag(newX, newY, clickType, changeX, changeY, oldX, oldY);
    }

    /**
     * @param x new X coordinate of the cursor
     * @param y new Y coordinate of the cursor
     * @param clickType the type of the click (LEFT, MIDDLE or RIGHT; see GLFW for more info)
     * @see org.lwjgl.glfw.GLFW GLFW
     * @param xChange the x movement the cursor has performed
     * @param yChange the y movement the cursor has performed
     * @param oldX the original x position of the cursor
     * @param oldY the original y position of the cursor
     * @return whether the even has been consumed
     */
    public boolean mouseDrag(double x, double y, int clickType, double xChange, double yChange, double oldX, double oldY) {
        return false;
    }

    public void mouseClick(double x, double y) {
    }

    public void mouseMove(double x, double y) {
    }

    public void mouseRelease(double x, double y) {
    }

    @Override
    public void setFocused(boolean pFocused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
