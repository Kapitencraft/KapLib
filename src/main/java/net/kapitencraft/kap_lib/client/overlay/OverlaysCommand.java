package net.kapitencraft.kap_lib.client.overlay;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class OverlaysCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("overlays")
                .then(Commands.literal("configure")
                        .executes(ClientHelper.createScreenCommand(ConfigureOverlaysScreen::new)))
                .then(Commands.literal("reset")
                        .executes(OverlaysCommand::reset)
                )
        );
    }

    private static int reset(CommandContext<CommandSourceStack> context) {
        OverlayManager.resetAll();
        CommandHelper.sendSuccess(context.getSource(), "command.overlays.reset.success");
        return 1;
    }
}
