package net.kapitencraft.kap_lib.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * server tests.
 */
public class CoreServerTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("tooltip")
                        .executes(CoreServerTestCommand::testLargeTooltip)
                )
        );
    }

    private static int testLargeTooltip(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            ItemStack stack = new ItemStack(Items.DIAMOND);
            List<String> loreData = List.of(
                    "Minecraft is a 2011 sandbox game developed and published by the Swedish video game developer Mojang Studios.",
                    "Originally created by Markus \"Notch\" Persson using the Java programming language, the first public alpha build was released on 17 May 2009.",
                    "The game was continuously developed from then on, receiving a full release on 18 November 2011.",
                    "Afterwards, Persson left Mojang and gave Jens \"Jeb\" Bergensten control over development.",
                    "In the years since its release, it has been ported to several platforms, including smartphones, tablets, and various video game consoles.",
                    "In 2014, Mojang and the Minecraft intellectual property were purchased by Microsoft for US$2.5 billion.",
                    "",
                    "In Minecraft, players explore a procedurally generated, three-dimensional world with virtually infinite terrain made up of voxels.",
                    "Players can discover and extract raw materials, craft tools and items, and build structures, earthworks, and machines.",
                    "Depending on their chosen game mode, players can fight hostile mobs, as well as cooperate with or compete against other players in multiplayer.",
                    "The game has two main modes: Survival mode, where players must acquire resources to survive, and Creative mode, where players have unlimited resources and the ability to fly.",
                    "The game can be further interacted with through Hardcore mode, a permadeath variant of Survival, as well as player-made downloadable maps or self-imposed challenges.",
                    "The game's large community offers a wide variety of user-generated content, such as modifications, servers, player skins, texture packs, and custom maps, which add new game mechanics and possibilities.",
                    "Speedrunning is another popular community activity.",
                    "",
                    "Minecraft is the best-selling video game of all time, with over 350 million copies sold and nearly 170 million monthly active players as of 2025.",
                    "In addition, it has received critical acclaim, winning several awards and being cited as one of the greatest video games of all time;",
                    "social media, parodies, adaptations, merchandise, and the annual Minecon conventions have played prominent roles in popularizing the game.",
                    "Minecraft has been used in educational environments to teach chemistry, computer-aided design, and computer science.",
                    "The wider Minecraft franchise includes several spin-off games including Minecraft: Story Mode, Minecraft Earth, Minecraft Dungeons, and Minecraft Legends.",
                    "A live-action film adaptation, titled A Minecraft Movie, was released in theatres on 4 April 2025. "
            );

            List<Component> list = loreData.stream().map(Component::literal).collect(Collectors.toUnmodifiableList());
            stack.set(DataComponents.LORE, new ItemLore(list));
            player.addItem(stack);
            return 1;
        });
    }
}
