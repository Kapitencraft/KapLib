package net.kapitencraft.kap_lib.attribute;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.attribute.timed.TimedModifierUtils;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * server tests.
 */
public class AttributeServerTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("timed_modifier")
                        .executes(AttributeServerTestCommand::testTimedModifier)
                )
        );
    }

    private static int testTimedModifier(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            TimedModifierUtils.add(player, LibConstants.res("test"), 400, Attributes.MAX_HEALTH, 4, AttributeModifier.Operation.ADD_VALUE);
            return 1;
        });
    }
}
