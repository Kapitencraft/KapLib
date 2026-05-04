package net.kapitencraft.kap_lib.component;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Objects;

/**
 * server tests.
 */
public class ComponentServerTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("player_head")
                        .executes(ComponentServerTestCommand::testPlayerHeadGlyph)
                )
        );
    }

    private static int testPlayerHeadGlyph(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            MinecraftServer server = Objects.requireNonNull(player.getServer());
            List<ServerPlayer> players = server.getPlayerList().getPlayers();
            MutableComponent text = Component.empty();
            for (ServerPlayer p : players) {
                text.append(ExtraComponents.playerHead(p.getUUID()));
            }
            player.sendSystemMessage(text);
            return 1;
        });
    }
}
