package net.kapitencraft.kap_lib.spawn_table;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnPoolEntries;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.ApiStatus;

@Mod(SpawnTableModule.MODULE_ID)
public class SpawnTableModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_spawn_table";

    public SpawnTableModule(IEventBus modEventBus, ModContainer container) {

        SpawnEntityFunctions.REGISTRY.register(modEventBus);
        SpawnPoolEntries.REGISTRY.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(SpawnTableModule::registerServerCommand);
    }

    @ApiStatus.Internal
    static void registerServerCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        SpawnTableServerTestCommand.register(dispatcher);
    }
}
