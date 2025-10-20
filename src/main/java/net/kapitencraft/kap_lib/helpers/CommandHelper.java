package net.kapitencraft.kap_lib.helpers;

import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.config.ServerModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public interface CommandHelper {
    /**
     * send a success message to the given {@link CommandSourceStack} automatically coloring it green
     */
    static void sendSuccess(CommandSourceStack stack, String msg, Object... args) {
        stack.sendSuccess(() -> Component.translatable(msg, args).withStyle(ChatFormatting.GREEN), true);
    }

    /**
     * check if the command was executed from the console and cancel it if so
     */
    static int checkNonConsoleCommand(CommandContext<CommandSourceStack> context, BiFunction<@NotNull ServerPlayer, CommandSourceStack, Integer> function) {
        CommandSourceStack stack = context.getSource();
        if (stack.getPlayer() != null) {
            return function.apply(stack.getPlayer(), stack);
        }
        stack.sendFailure(Component.translatable("command.failed.console").withStyle(ChatFormatting.RED));
        return 0;
    }

    /**
     * check if the given stack has Moderator permission
     */
    static boolean isModerator(CommandSourceStack stack) {
        return stack.hasPermission(1);
    }

    /**
     * check if the given stack has GameMaster permission
     */
    static boolean isGameMaster(CommandSourceStack stack) {
        return stack.hasPermission(2);
    }

    /**
     * check if the given stack has Admin permission
     */
    static boolean isAdmin(CommandSourceStack stack) {
        return stack.hasPermission(3);
    }

    /**
     * check if the given stack has Owner permission
     */
    static boolean isOwner(CommandSourceStack stack) {
        return stack.hasPermission(4);
    }

    /**
     * check if social commands are enabled
     */
    static boolean isSocialEnabled(CommandSourceStack stack) {
        return ServerModConfig.areSocialCommandsEnabled();
    }
}