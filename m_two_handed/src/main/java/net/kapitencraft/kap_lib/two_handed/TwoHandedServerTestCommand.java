package net.kapitencraft.kap_lib.two_handed;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TwoHandedServerTestCommand {
    private static final ResourceLocation FLAG = LibConstants.res("test");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("two_handed")
                        .then(Commands.literal("toggle")
                                .executes(TwoHandedServerTestCommand::toggle)
                        )
                )
        );
    }

    private static int toggle(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, stack) -> {
            if (player.kap_lib$hasSuppressionFlag(FLAG)) {
                player.kap_lib$removeSuppressionFlag(FLAG);
            } else {
                player.kap_lib$addSuppressionFlag(FLAG);
            }
            stack.sendSuccess(() -> Component.translatable("command.server_test.two_handed.toggle", player.kap_lib$suppressesTwoHanded()), true);
            return 1;
        });
    }
}
