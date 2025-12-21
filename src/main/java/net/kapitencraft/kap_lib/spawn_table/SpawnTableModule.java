package net.kapitencraft.kap_lib.spawn_table;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.requirement.registry.RequirementTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(SpawnTableModule.MODULE_ID)
public class SpawnTableModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_spawn_table";

    public SpawnTableModule(IEventBus modEventBus, ModContainer container) {
        RequirementTypes.REGISTRY.register(modEventBus);
    }
}
