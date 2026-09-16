package net.kapitencraft.kap_lib.multiblock.event.handlers;

import net.kapitencraft.kap_lib.multiblock.MultiblockModule;
import net.kapitencraft.kap_lib.multiblock.structure.MultiblockStructureConfigurationManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = MultiblockModule.MODULE_ID)
public class MBEvents {

    @SubscribeEvent
    public static void addBonusListener(AddReloadListenerEvent event) {
        event.addListener(MultiblockStructureConfigurationManager.INSTANCE);
    }
}
