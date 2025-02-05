package net.kapitencraft.kap_lib.helpers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.client.enchantment_color.ConfigureEnchantmentColorsCommand;
import net.kapitencraft.kap_lib.client.enchantment_color.ConfigureEnchantmentColorsScreen;
import net.kapitencraft.kap_lib.commands.OverlaysCommand;
import net.kapitencraft.kap_lib.commands.ClientTestCommand;
import net.kapitencraft.kap_lib.commands.ServerTestCommand;
import net.kapitencraft.kap_lib.config.ServerModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public interface CommandHelper {
    /**
     * register library commands
     */
    @ApiStatus.Internal
    static void registerClient(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        OverlaysCommand.register(dispatcher);
        ClientTestCommand.register(dispatcher);
        //1.20 teaser?
        ConfigureEnchantmentColorsCommand.register(dispatcher);
    }

    @ApiStatus.Internal
    static void registerServer(RegisterCommandsEvent event) {
        ServerTestCommand.register(event.getDispatcher());
    }

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