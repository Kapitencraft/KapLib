package net.kapitencraft.kap_lib.attribute;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(AttributeModule.MODULE_ID)
public class AttributeModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_attribute";

    public AttributeModule(IEventBus modEventBus, ModContainer container) {
        ExtraAttributes.REGISTRY.register(modEventBus);
        AttributeAttachmentTypes.REGISTRY.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(AttributeModule::registerServer);
    }

    @ApiStatus.Internal
    static void registerServer(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        AttributeServerTestCommand.register(dispatcher);
    }
}
