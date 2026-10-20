package net.kapitencraft.kap_lib.camera;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.camera.registry.CameraModifiers;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

@Mod(CameraModule.MODULE_ID)
public class CameraModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_camera";

    public CameraModule(IEventBus modEventBus, ModContainer container) {

        CameraModifiers.REGISTRY.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(CameraModule::registerClient);
    }

    /**
     * register library commands
     */
    @ApiStatus.Internal
    static void registerClient(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CameraClientTestCommand.register(dispatcher);
    }
}
