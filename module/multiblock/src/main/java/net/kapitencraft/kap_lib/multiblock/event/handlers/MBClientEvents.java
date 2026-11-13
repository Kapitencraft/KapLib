package net.kapitencraft.kap_lib.multiblock.event.handlers;

import net.kapitencraft.kap_lib.multiblock.registry.MBBlockEntityTypes;
import net.kapitencraft.kap_lib.multiblock.structure.config.builder.MultiblockStructureConfigurationBlockEntityRenderer;
import net.kapitencraft.kap_lib.multiblock.structure.config.builder.client.ConfiguratorItemHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber
public class MBClientEvents {

    @SubscribeEvent
    public static void onEntityRenderersRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MBBlockEntityTypes.MULTIBLOCK_STRUCTURE_CONFIG.get(), MultiblockStructureConfigurationBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onInputKey(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null)
            return;

        ConfiguratorItemHandler.onKeyInput(event.getKey());
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ConfiguratorItemHandler.tick();
    }
}
