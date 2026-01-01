package net.kapitencraft.kap_lib.spawn_table;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.kapitencraft.kap_lib.spawn_table.registry.SpawnTableRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;

/**
 * server tests.
 */
public class SpawnTableServerTestCommand {
    public static final ResourceKey<SpawnTable> TEST = ResourceKey.create(SpawnTableRegistries.Keys.SPAWN_TABLES, ResourceLocation.fromNamespaceAndPath("test", "test"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("spawn_table")
                        .executes(SpawnTableServerTestCommand::testSpawnTable)
                )
        );
    }

    private static int testSpawnTable(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            LootParams params = new LootParams.Builder(player.serverLevel())
                    .withParameter(LootContextParams.ORIGIN, commandSourceStack.getPosition())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.COMMAND);
            SpawnContext spawnContext = new SpawnContext.Builder(params)
                    .create(null);
            Optional<SpawnTable> table = player.registryAccess().lookupOrThrow(SpawnTableRegistries.Keys.SPAWN_TABLES).get(TEST).map(Holder::value);
            if (table.isPresent()) {
                table.get().getRandomEntities(spawnContext, entity ->
                        entity.setPos(commandSourceStack.getPosition())
                );
            } else {
                commandSourceStack.sendFailure(Component.translatable("command.server_test.spawn_table.not_found"));
            }
            return 1;
        });
    }
}