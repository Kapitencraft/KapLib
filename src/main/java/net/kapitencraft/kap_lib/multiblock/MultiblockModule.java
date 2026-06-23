package net.kapitencraft.kap_lib.multiblock;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.multiblock.registry.MBBlockEntityTypes;
import net.kapitencraft.kap_lib.multiblock.registry.MBBlocks;
import net.kapitencraft.kap_lib.multiblock.registry.MBItemComponentTypes;
import net.kapitencraft.kap_lib.multiblock.registry.MBItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(MultiblockModule.MODULE_ID)
public class MultiblockModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_multiblock";

    public MultiblockModule(IEventBus modEventBus, ModContainer container) {
        MBBlockEntityTypes.REGISTRY.register(modEventBus);
        MBBlocks.REGISTRY.register(modEventBus);
        MBItems.REGISTRY.register(modEventBus);
        MBItemComponentTypes.REGISTRY.register(modEventBus);
    }
}
