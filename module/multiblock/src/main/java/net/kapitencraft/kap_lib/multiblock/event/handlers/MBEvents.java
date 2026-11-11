package net.kapitencraft.kap_lib.multiblock.event.handlers;

import net.kapitencraft.kap_lib.multiblock.MultiblockModule;
import net.kapitencraft.kap_lib.multiblock.structure.MultiblockStructureManager;
import net.kapitencraft.kap_lib.multiblock.structure.MultiblockStructurePatternManager;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = MultiblockModule.MODULE_ID)
public class MBEvents {

    @SubscribeEvent
    public static void addBonusListener(AddReloadListenerEvent event) {
        event.addListener(MultiblockStructurePatternManager.INSTANCE);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockEntityPlace(BlockEvent.EntityPlaceEvent event) {
        MultiblockStructureManager.INSTANCE.notifyChange(event.getLevel(), event.getBlockSnapshot(), event.getState());
    }
}
