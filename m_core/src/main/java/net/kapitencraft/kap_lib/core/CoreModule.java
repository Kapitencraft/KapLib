package net.kapitencraft.kap_lib.core;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.core.config.CoreClientModConfig;
import net.kapitencraft.kap_lib.core.config.ServerModConfig;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * necessary module for all other modules
 */
@Mod(CoreModule.MODULE_ID)
public class CoreModule {

    public static final String MODULE_ID = LibConstants.MOD_ID + "_core";

    public CoreModule(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CoreClientModConfig.SPEC);
        container.registerConfig(ModConfig.Type.SERVER, ServerModConfig.SPEC);

        NeoForge.EVENT_BUS.addListener(CoreModule::registerServer);
        NeoForge.EVENT_BUS.addListener(CoreModule::onRegisterClientCommands);
    }

    @ApiStatus.Internal
    static void registerServer(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CoreServerTestCommand.register(dispatcher);
    }

    @ApiStatus.Internal
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CoreClientTestCommand.register(event.getDispatcher());
    }
}
