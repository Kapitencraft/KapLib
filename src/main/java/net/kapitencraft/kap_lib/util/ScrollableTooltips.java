package net.kapitencraft.kap_lib.util;

import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector2i;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ScrollableTooltips {
    private static int scrollY = 0;
    private static int initY = 0;
    private static float scale = 1;
    private static int oldTooltipSize = 0;
    private static int stackHash = 0;

    @SubscribeEvent
    public static void registerScrollable(RenderTooltipEvent.Pre event) {
        Vector2i toolTipSize = createToolTipBoxSize(event.getComponents(), event.getFont());
        Vector2i screenSize = new Vector2i(event.getScreenWidth(), event.getScreenHeight());
        Vector2i pos = new Vector2i(event.getX(), event.getY());
        int height = event.getY();
        boolean isHigherThanScreen = toolTipSize.y > screenSize.y || height + toolTipSize.y > screenSize.y;
        if (stackHash != event.getComponents().hashCode()) {
            scrollY = 0;
            stackHash = event.getComponents().hashCode();
        }
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
        if (stackHash != 0) {
            //event.setCanceled(true); //TODO fix
            float scrollDelta = (float) event.getScrollDelta();
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