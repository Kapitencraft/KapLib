package net.kapitencraft.kap_lib.multiblock;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.Orientation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class MultiblockServerTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("orientation")
                        .executes(MultiblockServerTestCommand::testOrientation)
                )
        );
    }

    private static int testOrientation(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            BlockPos pos = player.getOnPos();
            BlockPos offset = new BlockPos(5, 0, 5);
            Level level = player.level();
            Block[] blocks = new Block[] {
                    Blocks.NETHERITE_BLOCK,
                    Blocks.GOLD_BLOCK,
                    Blocks.REDSTONE_BLOCK,
                    Blocks.EMERALD_BLOCK,
                    Blocks.DIAMOND_BLOCK,
                    Blocks.COAL_BLOCK,
                    Blocks.COPPER_BLOCK,
                    Blocks.AMETHYST_BLOCK,
                    Blocks.POLISHED_DIORITE,
                    Blocks.POLISHED_GRANITE,
                    Blocks.MOSS_BLOCK,
                    Blocks.LIME_CONCRETE
            };
            for (int i = 0; i < Orientation.values().length; i++) {
                BlockPos blockPos = Orientation.values()[i].rotate(offset).offset(pos);
                if (level.getBlockState(blockPos).isAir())
                    level.setBlockAndUpdate(blockPos, blocks[i].defaultBlockState());
            }
            return 1;
        });
    }

}
