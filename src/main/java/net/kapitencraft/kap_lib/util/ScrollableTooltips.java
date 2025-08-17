package net.kapitencraft.kap_lib.util;

import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector2i;

import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
@ApiStatus.Internal
public class ScrollableTooltips {
    private static int scrollY = 0;
    private static int initY = 0;
    private static float scale = 1;
    private static boolean allowScroll;
    private static int oldTooltipSize = 0;
    private static Slot active;

    @SubscribeEvent
    public static void onRenderForeground(ContainerScreenEvent.Render.Foreground event) {
        AbstractContainerScreen<?> screen = event.getContainerScreen();
        if (screen.hoveredSlot != active) {
            scrollY = 0;
            active = screen.hoveredSlot;
        }
    }


    @SubscribeEvent
    public static void registerScrollable(RenderTooltipEvent.Pre event) {
        Vector2i toolTipSize = createToolTipBoxSize(event.getComponents(), event.getFont());
        Vector2i screenSize = new Vector2i(event.getScreenWidth(), event.getScreenHeight());
        Vector2i pos = new Vector2i(event.getX(), event.getY());
        int height = event.getY();
        boolean isHigherThanScreen = allowScroll = toolTipSize.y > screenSize.y;
        if (scrollY == 0 || !isHigherThanScreen) {
            int i = toolTipSize.y + 3;
            if (pos.y + i > screenSize.y) {
                height = screenSize.y - i;
            }
        }
        if (isHigherThanScreen) {
            if (oldTooltipSize != toolTipSize.y && oldTooltipSize != 0) {
                scrollY *= (int) ((double) toolTipSize.y / oldTooltipSize);
                oldTooltipSize = toolTipSize.y;
            }
            if (scrollY == 0) initY = height;
            event.setY(initY + scrollY);
            return;
        }
        event.setY(height);
    }

    @SubscribeEvent
    public static void scrollEvent(ScreenEvent.MouseScrolled.Pre event) {
        if (active != null && active.hasItem() && allowScroll) {
            event.setCanceled(true);
            float scrollDelta = (float) event.getScrollDeltaY();
            int scrollOffset = Mth.floor(scrollDelta * ClientModConfig.getScrollScale());
            if (Screen.hasControlDown()) {
                scale += scrollOffset;
            } else {
                scrollY -= scrollOffset;
            }
        }
    }

    private static Vector2i createToolTipBoxSize(List<ClientTooltipComponent> components, Font font) {
        int i = 0, j = 0;
        for(ClientTooltipComponent clienttooltipcomponent : components) {
            int k = clienttooltipcomponent.getWidth(font);
            if (k > i) {
                i = k;
            }

            j += clienttooltipcomponent.getHeight();
        }
        return new Vector2i(i, j);
    }
}