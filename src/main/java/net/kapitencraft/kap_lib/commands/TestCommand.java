package net.kapitencraft.kap_lib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kapitencraft.kap_lib.client.gui.screen.TestScreen;
import net.kapitencraft.kap_lib.client.particle.ShimmerShieldParticleOptions;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.CommandHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.ModGlyphEffects;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class TestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lib_test")
                .then(Commands.literal("screen")
                        .executes(commandContext -> {
                            ClientHelper.postCommandScreen = new TestScreen();
                            return 1;
                        })
                ).then(Commands.literal("particle")
                        .executes(TestCommand::spawnParticle)
                ).then(Commands.literal("chroma")
                        .executes(TestCommand::testChroma)
                )
        );
    }

    private static int testChroma(CommandContext<CommandSourceStack> commandContext) {
        for (int i = 0; i < 10; i++) commandContext.getSource().sendSystemMessage(Component.literal("EEEEEEEEEEEEEEEEEE").setStyle(MiscHelper.withSpecial(Style.EMPTY, ModGlyphEffects.RAINBOW.get())));
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
