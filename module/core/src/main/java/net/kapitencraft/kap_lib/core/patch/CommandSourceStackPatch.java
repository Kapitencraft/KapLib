package net.kapitencraft.kap_lib.core.patch;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public interface CommandSourceStackPatch {

    /**
     * send a green success message
     */
    default void sendSuccess(String msg, Object... args) {
        ((CommandSourceStack) this).sendSuccess(() -> Component.translatable(msg, args).withStyle(ChatFormatting.GREEN), true);
    }

    /**
     * check if the given stack has Moderator permission
     */
    default boolean isModerator() {
        return ((CommandSourceStack) this).hasPermission(1);
    }

    /**
     * check if the given stack has GameMaster permission
     */
    default boolean isGameMaster() {
        return ((CommandSourceStack) this).hasPermission(2);
    }

    /**
     * check if the given stack has Admin permission
     */
    default boolean isAdmin() {
        return ((CommandSourceStack) this).hasPermission(3);
    }

    /**
     * check if the given stack has Owner permission
     */
    default boolean isOwner() {
        return ((CommandSourceStack) this).hasPermission(4);
    }
}
