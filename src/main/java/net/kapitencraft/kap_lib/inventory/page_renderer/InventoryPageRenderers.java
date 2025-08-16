package net.kapitencraft.kap_lib.inventory.page_renderer;

import net.kapitencraft.kap_lib.event.custom.client.RegisterInventoryPageRenderersEvent;
import net.kapitencraft.kap_lib.inventory.page.InventoryPage;
import net.kapitencraft.kap_lib.inventory.page.InventoryPageType;
import net.kapitencraft.kap_lib.inventory.page.crafting.CraftingPageRenderer;
import net.kapitencraft.kap_lib.inventory.page.equipment.EquipmentPageRenderer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaInventoryPages;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class InventoryPageRenderers {

    @SuppressWarnings("unchecked")
    public static <P extends InventoryPage> PageRenderConstructor<P> getRenderer(P page) {
        return Objects.requireNonNull((PageRenderConstructor<P>) renderers.get(page.getType()), "no renderer for inventory page: '" + ExtraRegistries.INVENTORY_PAGES.getKey(page.getType()) + "'");
    }

    public static void init() {
        register(VanillaInventoryPages.EQUIPMENT.get(), EquipmentPageRenderer::new);
        register(VanillaInventoryPages.CRAFTING.get(), CraftingPageRenderer::new);
        ModLoader.postEvent(new RegisterInventoryPageRenderersEvent());
    }

    private static final Map<InventoryPageType<?>, PageRenderConstructor<?>> renderers = new HashMap<>();

    /**
     * use the event {@link RegisterInventoryPageRenderersEvent}
     */
    @ApiStatus.Internal
    public static <P extends InventoryPage> void register(InventoryPageType<P> type, PageRenderConstructor<P> renderer) {
        if (renderers.put(type, renderer) != null) {
            throw new IllegalArgumentException("duplicate inventory page renderer registration: " + ExtraRegistries.INVENTORY_PAGES.getKey(type));
        }
    }

    public interface PageRenderConstructor<P extends InventoryPage> {
        InventoryPageRenderer construct(P page);
    }
}
