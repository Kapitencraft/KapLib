package net.kapitencraft.kap_lib.multiblock;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.multiblock.registry.ModBlockEntityTypes;
import net.kapitencraft.kap_lib.multiblock.registry.ModBlocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(MultiblockModule.MODULE_ID)
public class MultiblockModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_multiblock";

    public MultiblockModule(IEventBus modEventBus, ModContainer container) {
        ModBlockEntityTypes.REGISTRY.register(modEventBus);
        ModBlocks.REGISTRY.register(modEventBus);
    }
}
