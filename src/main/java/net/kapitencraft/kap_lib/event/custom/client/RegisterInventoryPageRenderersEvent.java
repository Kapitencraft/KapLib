package net.kapitencraft.kap_lib.event.custom.client;

import net.kapitencraft.kap_lib.inventory.page.InventoryPage;
import net.kapitencraft.kap_lib.inventory.page.InventoryPageType;
import net.kapitencraft.kap_lib.inventory.page_renderer.InventoryPageRenderers;
import net.kapitencraft.kap_lib.inventory.page_renderer.InventoryPageRenderer;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.Supplier;

/**
 * used to register InventoryPageRenderers
 */
public class RegisterInventoryPageRenderersEvent extends Event implements IModBusEvent {

    public <P extends InventoryPage> void register(InventoryPageType<P> type, InventoryPageRenderers.PageRenderConstructor<P> renderer) {
        InventoryPageRenderers.register(type, renderer);
    }

    public <P extends InventoryPage> void register(Supplier<InventoryPageType<P>> type, InventoryPageRenderers.PageRenderConstructor<P> renderer) {
        this.register(type.get(), renderer);
    }
}
