package net.kapitencraft.kap_lib.core;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.core.client.gui.screen.TestScreen;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CoreClientTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("client_test")
                .then(Commands.literal("text_box")
                        .executes(CommandHelper.createScreenCommand(TestScreen::new))
                )
        );
    }
}
