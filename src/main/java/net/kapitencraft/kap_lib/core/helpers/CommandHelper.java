package net.kapitencraft.kap_lib.core.helpers;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.core.config.ServerModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CommandHelper {
    /**
     * use {@link #createScreenCommand(Supplier)} instead
     */
    @ApiStatus.Internal
    public static Screen postCommandScreen = null;

    /**
     * @param creator a supplier for the screen to be opened
     * @return the command to be inserted into {@link com.mojang.brigadier.builder.ArgumentBuilder#executes(Command) ArgumentBuilder#executes} 
     */
    public static Command<CommandSourceStack> createScreenCommand(Supplier<Screen> creator) {
        return stack -> {
            postCommandScreen = creator.get();
            return 1;
        };
    }

    /**
     * send a success message to the given {@link CommandSourceStack} automatically coloring it green
     */
    public static void sendSuccess(CommandSourceStack stack, String msg, Object... args) {
        stack.sendSuccess(() -> Component.translatable(msg, args).withStyle(ChatFormatting.GREEN), true);
    }

    /**
     * check if the command was executed from the console and cancel it if so
     */
    public static int checkNonConsoleCommand(CommandContext<CommandSourceStack> context, BiFunction<@NotNull ServerPlayer, CommandSourceStack, Integer> function) {
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
    public static boolean isModerator(CommandSourceStack stack) {
        return stack.hasPermission(1);
    }

    /**
     * check if the given stack has GameMaster permission
     */
    public static boolean isGameMaster(CommandSourceStack stack) {
        return stack.hasPermission(2);
    }

    /**
     * check if the given stack has Admin permission
     */
    public static boolean isAdmin(CommandSourceStack stack) {
        return stack.hasPermission(3);
    }

    /**
     * check if the given stack has Owner permission
     */
    public static boolean isOwner(CommandSourceStack stack) {
        return stack.hasPermission(4);
    }
}