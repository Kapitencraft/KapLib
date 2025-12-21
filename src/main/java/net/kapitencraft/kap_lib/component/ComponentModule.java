package net.kapitencraft.kap_lib.component;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.commands.ComponentClientTestCommand;
import net.kapitencraft.kap_lib.component.config.ComponentClientModConfig;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

@Mod(ComponentModule.MODULE_ID)
public class ComponentModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_component";

    public ComponentModule(IEventBus modEventBus, ModContainer container) {

        NeoForge.EVENT_BUS.addListener(ComponentModule::registerClient);

        container.registerConfig(ModConfig.Type.CLIENT, ComponentClientModConfig.SPEC);
    }

    /**
     * register library commands
     */
    @ApiStatus.Internal
    static void registerClient(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ComponentClientTestCommand.register(dispatcher);
    }
}
