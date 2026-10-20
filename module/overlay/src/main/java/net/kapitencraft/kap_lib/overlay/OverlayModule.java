package net.kapitencraft.kap_lib.overlay;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

@Mod(OverlayModule.MODULE_ID)
public class OverlayModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_overlay";

    public OverlayModule(IEventBus modEventBus, ModContainer container) {
        NeoForge.EVENT_BUS.addListener(OverlayModule::registerCommand);
    }

    /**
     * register library commands
     */
    @ApiStatus.Internal
    static void registerCommand(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        OverlaysCommand.register(dispatcher);
    }
}