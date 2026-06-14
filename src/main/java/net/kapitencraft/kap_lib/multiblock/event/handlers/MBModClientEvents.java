package net.kapitencraft.kap_lib.multiblock.event.handlers;

import net.kapitencraft.kap_lib.multiblock.registry.MBBlockEntityTypes;
import net.kapitencraft.kap_lib.multiblock.structure.config.builder.MultiblockStructureConfigurationBlockEntityRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber
public class MBModClientEvents {

    @SubscribeEvent
    public static void onEntityRenderersRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MBBlockEntityTypes.MULTIBLOCK_STRUCTURE_CONFIG.get(), MultiblockStructureConfigurationBlockEntityRenderer::new);
    }
}
