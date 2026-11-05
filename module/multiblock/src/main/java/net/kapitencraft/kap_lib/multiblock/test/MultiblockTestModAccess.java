package net.kapitencraft.kap_lib.multiblock.test;

import net.kapitencraft.kap_lib.multiblock.MultiblockModule;
import net.kapitencraft.kap_lib.multiblock.test.registry.TestBlockEntityTypes;
import net.kapitencraft.kap_lib.multiblock.test.registry.TestBlocks;
import net.kapitencraft.kap_lib.multiblock.test.registry.TestItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MultiblockModule.MODULE_ID)
public class MultiblockTestModAccess {

    public MultiblockTestModAccess(IEventBus modEventBus) {

        TestBlockEntityTypes.REGISTRY.register(modEventBus);
        TestBlocks.REGISTRY.register(modEventBus);
        TestItems.REGISTRY.register(modEventBus);
    }
}