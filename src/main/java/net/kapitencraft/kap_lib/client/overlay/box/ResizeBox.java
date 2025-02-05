package net.kapitencraft.kap_lib.client.overlay.box;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.gui.screen.MenuableScreen;
import net.kapitencraft.kap_lib.client.overlay.OverlayManager;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.overlay.holder.Overlay;
import net.kapitencraft.kap_lib.client.gui.IMenuBuilder;
import net.kapitencraft.kap_lib.client.widget.menu.Menu;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements.ButtonElement;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements.EnumElement;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * box used to resize other things (like a RenderHolder)
 */
public class ResizeBox extends InteractiveBox implements IMenuBuilder {
    private final List<AccessBox> boxes = new ArrayList<>();
    private final @NotNull Overlay dedicatedHolder;
    private static final int BOX_COLOR = 0xFFFFFFFF;
    private static final int FILL_COLOR = 0x30FFFFFF;
    private AccessBox active;

    public ResizeBox(Vec2 start, Vec2 finish, @NotNull Overlay dedicatedHolder) {
        super(start, finish, GLFW.GLFW_RESIZE_ALL_CURSOR, FILL_COLOR);
        this.dedicatedHolder = dedicatedHolder;
        fillBoxes();
    }

    private void fillBoxes() {
        this.boxes.clear();
        for (Type type : Type.values()) {
            boxes.add(new AccessBox(type == Type.C ? FILL_COLOR : BOX_COLOR, type));
        }
        reapplyPosition();
    }

    @Override
    public void render(GuiGraphics graphics, double mouseX, double mouseY) {
        boxes.forEach(resizeAccessBox -> resizeAccessBox.render(graphics, mouseX, mouseY));
    }

    @Override
    public int getCursorType(double mouseX, double mouseY) {
        return boxes.stream().filter(resizeAccessBox -> resizeAccessBox.isMouseOver(mouseX, mouseY))
                .map(box -> box.getCursorType(mouseX, mouseY))
                .findFirst().orElse(getSelfCursor(mouseX, mouseY));
    }

    private int getSelfCursor(double mouseX, double mouseY) {
        int cursorId = GLFW.GLFW_ARROW_CURSOR;
        if (this.isMouseOver(mouseX, mouseY)) cursorId = GLFW.GLFW_RESIZE_ALL_CURSOR;
        return cursorId;
    }

    public void move(Vec2 delta) {
        this.dedicatedHolder.move(delta);
        moveWithoutHolder(delta);
    }

    @Override
    public void moveX(float offset) {
        super.moveX(offset);
        this.dedicatedHolder.moveX(offset);
    }

    @Override
    public void moveY(float offset) {
        super.moveY(offset);
        this.dedicatedHolder.moveY(offset);
    }

    private void addX(float xChange) {
        this.end = new Vec2(this.end.x + xChange, this.end.y);
    }

    private void addY(float yChange) {
        this.end = new Vec2(this.end.x, this.end.y + yChange);
    }

    private void moveWithoutHolder(Vec2 delta) {
        super.move(delta);
        this.boxes.forEach(resizeAccessBox -> resizeAccessBox.move(delta));
    }

    protected void reapplyPosition() {
        this.boxes.forEach(AccessBox::reapplyPosition);
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        return super.isMouseOver(x, y) || boxes.stream().anyMatch(resizeAccessBox -> resizeAccessBox.isMouseOver(x, y));
    }

    @Override
    public boolean mouseDrag(double x, double y, int clickType, double xChange, double yChange, double oldX, double oldY) {
        if (active == null) return false;
        Type type = active.getType();
        if (type == Type.C) {
            this.move(new Vec2((float) xChange, (float) yChange));
        } else {
            Boolean xOffset = active.getType().axes.get(Axis.X);
            Boolean yOffset = active.getType().axes.get(Axis.Y);
            if (xOffset != null) {
                if (xOffset) this.moveX((float) xChange);
                else this.addX((float) xChange);
                float width = width();
                float scaleX = (float) ((width + (xOffset ? -xChange : xChange)) / width);
                this.dedicatedHolder.getProperties().scaleX(scaleX);
            }
            if (yOffset != null) {
                if (yOffset) this.moveY((float) yChange);
                else this.addY((float) yChange);
                float height = height();
                float scaleY = (float) ((height + (yOffset ? -yChange : yChange)) / height);
                this.dedicatedHolder.getProperties().scaleY(scaleY);
            }
        }
        this.reapplyPosition();
        return true;
    }

    protected Type getType() {
        return this.active.getType();
    }

    @Override
    public void mouseRelease(double x, double y) {
        this.active = null;
    }

    private void setActive(AccessBox box) {
        this.active = box;
    }

    @Override
    public Menu createMenu(int x, int y, MenuableScreen screen) {
        DropDownMenu menu = new DropDownMenu(x, y, this);
        OverlayProperties properties = this.dedicatedHolder.getProperties();
        menu.addElement(EnumElement.builder(OverlayProperties.Alignment.class)
                .setName(Component.translatable("gui.alignment.x"))
                .setCurrent(properties.getXAlignment())
                .setElements(OverlayProperties.Alignment.values())
                .setNameMapper(OverlayProperties.Alignment::getWidthName)
                .setOnChange(properties::setXAlignment)
        );
        menu.addElement(EnumElement.builder(OverlayProperties.Alignment.class)
                .setName(Component.translatable("gui.alignment.y"))
                .setCurrent(properties.getYAlignment())
                .setElements(OverlayProperties.Alignment.values())
                .setNameMapper(OverlayProperties.Alignment::getHeightName)
                .setOnChange(properties::setYAlignment)
        );
        menu.addElement(ButtonElement.builder()
                .setName(Component.translatable("gui.reset_overlay"))
                .setExecutor(this::reset)
        );
        menu.addElement(ButtonElement.builder()
                .setName(Component.translatable("gui.hide_overlay"))
                .setExecutor(() -> {
                    OverlayManager.setVisible(this.dedicatedHolder, false);
                    properties.hide();
                    menu.hide(screen);
                    screen.closeMenu();
                    
                })
        );
        return menu;
    }

    private void reset() {
        OverlayManager controller = LibClient.overlays;
        controller.reset(this.dedicatedHolder);
        this.dedicatedHolder.reset(ClientHelper.getScreenWidth(), ClientHelper.getScreenHeight(), Minecraft.getInstance().player, Minecraft.getInstance().font, this);
        this.reapplyPosition();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            Optional<AccessBox> optional = boxes.stream().filter(accessBox -> accessBox.isMouseOver(pMouseX, pMouseY)).findFirst();
            optional.ifPresent(this::setActive);
            return optional.isPresent();
        }
        return pButton == 1 && this.isMouseOver(pMouseX, pMouseY);
    }

    public int getActiveArrowId() {
        return this.active == null ? GLFW.GLFW_ARROW_CURSOR : this.active.getCursorType();
    }

    public enum Type {
        N(Map.of(Axis.Y, true)),
        NE(Map.of(Axis.Y, true, Axis.X, false)),
        E(Map.of(Axis.X, false)),
        SE(Map.of(Axis.Y, false, Axis.X, false)),
        S(Map.of(Axis.Y, false)),
        SW(Map.of(Axis.Y, false, Axis.X, true)),
        W(Map.of(Axis.X, true)),
        NW(Map.of(Axis.Y, true, Axis.X, false)),
        C(Map.of());

        private final Map<Axis, Boolean> axes;

        Type(Map<Axis, Boolean> map) {
            this.axes = map;
        }
    }

    private enum Axis {
        X,
        Y
    }

    private class AccessBox extends InteractiveBox {
        private final ResizeBox.Type type;
        protected AccessBox(Vec2 start, Vec2 finish, int cursorType, int color, ResizeBox.Type type) {
            super(start, finish, cursorType, color);
            this.type = type;
        }

        protected AccessBox(int color, ResizeBox.Type type) {
            this(Vec2.ZERO, Vec2.ZERO, getCursorType(type), color, type);
        }

        protected ResizeBox.Type getType() {
            return type;
        }

        public static int getCursorType(ResizeBox.Type type) {
            return switch (type) {
                case E,W -> GLFW.GLFW_RESIZE_EW_CURSOR;
                case N,S -> GLFW.GLFW_RESIZE_NS_CURSOR;
                case NW,SE -> GLFW.GLFW_RESIZE_NWSE_CURSOR;
                case NE,SW -> GLFW.GLFW_RESIZE_NESW_CURSOR;
                case C -> GLFW.GLFW_RESIZE_ALL_CURSOR;
            };
        }

        public int getCursorType() {
            return getCursorType(this.type);
        }

        private static final float LINE_WIDTH = 0.5f;
        private static final float SQUARE_SIZE = 1.5f;

        protected void reapplyPosition() {
            Vec2 start = ResizeBox.this.start;
            Vec2 end = ResizeBox.this.end;
            Vec2 bottomLeft = new Vec2(start.x, end.y);
            Vec2 topRight = new Vec2(end.x, start.y);
            switch (this.type) {
                case E -> this.applyLine(topRight, end, LINE_WIDTH);
                case SE -> this.applySquare(end, SQUARE_SIZE);
                case S -> this.applyLine(bottomLeft, end, LINE_WIDTH);
                case SW -> this.applySquare(bottomLeft, SQUARE_SIZE);
                case W -> this.applyLine(start, bottomLeft, LINE_WIDTH);
                case NW -> this.applySquare(start, SQUARE_SIZE);
                case N -> this.applyLine(start, topRight, LINE_WIDTH);
                case NE -> this.applySquare(topRight, SQUARE_SIZE);
                case C -> {
                    this.start = start.add(LINE_WIDTH);
                    this.end = end.add(-LINE_WIDTH);
                }
            }
        }

        public void applyLine(Vec2 start, Vec2 finish, float lineW) {
            boolean horizontal = start.x == finish.x;
            this.start = new Vec2(horizontal ? start.x - lineW : start.x, horizontal ? start.y : start.y - lineW);
            this.end = new Vec2(horizontal ? finish.x + lineW : finish.x, horizontal ? finish.y : finish.y  + lineW);
        }

        public void applySquare(Vec2 center, float size) {
            this.start = center.add(new Vec2(-size, -size));
            this.end = center.add(new Vec2(size, size));
        }
    }
}
