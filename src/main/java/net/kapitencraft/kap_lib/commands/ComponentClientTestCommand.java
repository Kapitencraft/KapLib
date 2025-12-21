package net.kapitencraft.kap_lib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.client.gui.screen.TestScreen;
import net.kapitencraft.kap_lib.component.ExtraComponents;
import net.kapitencraft.kap_lib.component.player_head.PlayerHeadAllocator;
import net.kapitencraft.kap_lib.component.registry.GlyphEffects;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ComponentClientTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("client_test")
                .then(Commands.literal("chroma")
                        .executes(ComponentClientTestCommand::testChroma)
                ).then(Commands.literal("glyph").executes(ComponentClientTestCommand::testGlyphs)
                        .then(Commands.literal("reset").executes(ComponentClientTestCommand::resetGlyphs)
                        )
                )
        );
    }

    private static int resetGlyphs(CommandContext<CommandSourceStack> context) {
        PlayerHeadAllocator.getInstance().reset();
        return 1;
    }

    private static int testGlyphs(CommandContext<CommandSourceStack> context) {
        Player player = Minecraft.getInstance().player;
        UUID uuid = player.getUUID();

        player.sendSystemMessage(ExtraComponents.playerHead(uuid));
        return 1;
    }

    private static int testChroma(CommandContext<CommandSourceStack> commandContext) {
        for (int i = 0; i < 10; i++)
            commandContext.getSource().sendSystemMessage(Component.literal("EEEEEEEEEEEEEEEEEE").setStyle(MiscHelper.withSpecial(Style.EMPTY, GlyphEffects.RAINBOW)));
        return 1;
    }
}
