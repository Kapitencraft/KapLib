package net.kapitencraft.kap_lib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kapitencraft.kap_lib.client.gui.screen.TestScreen;
import net.kapitencraft.kap_lib.client.particle.ShimmerShieldParticleOptions;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.CommandHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.GlyphEffects;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.UUID;

public class ClientTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("client_test")
                    .then(Commands.literal("screen")
                        .executes(commandContext -> {
                            ClientHelper.postCommandScreen = new TestScreen();
                            return 1;
                        })
                ).then(Commands.literal("particle")
                        .executes(ClientTestCommand::spawnParticle)
                ).then(Commands.literal("chroma")
                        .executes(ClientTestCommand::testChroma)
                )
        );
    }

    private static int testChroma(CommandContext<CommandSourceStack> commandContext) {
        for (int i = 0; i < 10; i++) commandContext.getSource().sendSystemMessage(Component.literal("EEEEEEEEEEEEEEEEEE").setStyle(MiscHelper.withSpecial(Style.EMPTY, GlyphEffects.RAINBOW.get())));
        return 1;
    }

    private static int spawnParticle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().getEntityOrException()
                .level()
                .addParticle(
                        new ShimmerShieldParticleOptions(100, 50, Minecraft.getInstance().player.getId(), 5, 10, 1000, new Color(0xFFFF0000), new Color(0xFF00FF00), .01f, UUID.randomUUID()),
                        true, 0, 0, 0, 0, 0, 0
                        );
        CommandHelper.sendSuccess(context.getSource(), "success!");
        return 1;
    }
}