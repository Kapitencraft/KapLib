package net.kapitencraft.kap_lib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.client.ExtraComponents;
import net.kapitencraft.kap_lib.client.particle.LightningParticle;
import net.kapitencraft.kap_lib.client.particle.animation.AnimationUtils;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.client.particle.animation.elements.RotateElement;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.RemoveParticleFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.SetLifeTimeFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.elements.KeepAliveElement;
import net.kapitencraft.kap_lib.client.particle.animation.elements.MoveAwayElement;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.LineSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.client.util.rot_target.RotationTarget;
import net.kapitencraft.kap_lib.helpers.CommandHelper;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.SpawnTableManager;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Objects;

/**
 * server tests.
 */
public class ServerTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("animation")
                        .then(Commands.literal("rotation")
                                .executes(ServerTestCommand::testRotation)
                        ).then(Commands.literal("arrow")
                                .executes(ServerTestCommand::testArrow)
                        ).then(Commands.literal("aura")
                                .executes(ServerTestCommand::testAura)
                        ).then(Commands.literal("star")
                                .executes(ServerTestCommand::testStar)
                        ).then(Commands.literal("line")
                                .executes(ServerTestCommand::testLine)
                        )
                ).then(Commands.literal("spawn_table")
                        .executes(ServerTestCommand::testSpawnTable)
                ).then(Commands.literal("player_head")
                        .executes(ServerTestCommand::testPlayerHeadGlyph)
                ).then(Commands.literal("tooltip")
                        .executes(ServerTestCommand::testLargeTooltip)
                )
        );
    }

    private static int testLargeTooltip(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            ItemStack stack = new ItemStack(Items.DIAMOND);
            CompoundTag tag = stack.getOrCreateTagElement("display");
            ListTag lore = new ListTag();
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
            loreData.stream().map(Component::literal).map(Component.Serializer::toJson).map(StringTag::valueOf).forEach(lore::add);
            tag.put("Lore", lore);
            player.addItem(stack);
            return 1;
        });
    }

    private static int testPlayerHeadGlyph(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            MinecraftServer server = Objects.requireNonNull(player.getServer());
            List<ServerPlayer> players = server.getPlayerList().getPlayers();
            MutableComponent text = Component.empty();
            for (ServerPlayer p : players) {
                text.append(ExtraComponents.playerHead(p.getUUID()));
            }
            player.sendSystemMessage(text);
            return 1;
        });
    }

    private static int testSpawnTable(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            LootParams params = new LootParams.Builder(player.serverLevel())
                    .withParameter(LootContextParams.ORIGIN, commandSourceStack.getPosition())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.COMMAND);
            SpawnContext spawnContext = new SpawnContext.Builder(params)
                    .create(null);
            SpawnTable table = SpawnTableManager.instance.getSpawnTable(new ResourceLocation("test:test"));
            table.getRandomEntities(spawnContext, entity ->
                    entity.setPos(commandSourceStack.getPosition())
            );
            return 1;
        });
    }

    private static int testStar(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            PositionTarget center = PositionTarget.fixed(player.position());
            AnimationUtils.star(5, ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.FLAME, .25f, 5f, center)
                    .terminatedWhen(TimedTerminator.ticks(600))
                    .finalizes(RemoveParticleFinalizer.builder())
                    .then(RotateElement.builder()
                            .angle(1)
                            .axis(Direction.Axis.Y)
                            .pivot(center)
                            .duration(600)
                    )
                    .sendToPlayer(player);
            return 1;
        });
    }

    private static int testLine(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            Vec3 pos = player.position();
            ParticleAnimation.builder()
                    .spawnTime(ParticleAnimation.SpawnTime.once())
                    .spawn(LineSpawner.builder()
                            .start(PositionTarget.fixed(pos))
                            .end(PositionTarget.fixed(pos.add(0, 10, 0)))
                            .spacing(.25f)
                            .setParticle(ParticleTypes.FLAME)
                    )
                    .terminatedWhen(TimedTerminator.seconds(10))
                    .finalizes(RemoveParticleFinalizer.builder())
                    .then(KeepAliveElement.forDuration(200))
                    .sendToPlayer(player);
            return 1;
        });
    }

    private static int testAura(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            ParticleAnimation.builder()
                    .spawnTime(ParticleAnimation.SpawnTime.absolute(1))
                    .finalizes(SetLifeTimeFinalizer.builder().resetAge().lifeTime(20))
                    .spawn(RingSpawner.entityWithBBSize(player, 1.7f, 1f)
                            .setParticle(ParticleTypes.FLAME)
                            .rotPerTick(5)
                            .heightPerTick(.02f)
                    )
                    .terminatedWhen(TimedTerminator.ticks(600))
                    .sendToPlayer(player);
            return 1;
        });

    }

    private static int testArrow(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            Arrow arrow = new Arrow(player.level(), player);
            arrow.setPos(player.position());
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 0.5f, 0f);
            player.level().addFreshEntity(arrow);
            ParticleAnimation.requireEntity(arrow)
                    .spawnTime(ParticleAnimation.SpawnTime.absolute(1))
                    .finalizes(SetLifeTimeFinalizer.builder().resetAge().lifeTime(20))
                    .spawn(RingSpawner.noHeight()
                            .setTarget(PositionTarget.entity(arrow))
                            .rotation(RotationTarget.forEntity(arrow))
                            .spawnCount(2)
                            .rotPerTick(5)
                            .axis(Direction.Axis.Z)
                            .radius(.3f)
                            .setParticle(new DustParticleOptions(Vec3.fromRGB24(0xFFFFFF).toVector3f(), .34f))
                    )
                    .sendToPlayer(player);
            return 1;
        });
    }

    private static int testRotation(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            Vec3 playerPos = player.position();
            ParticleAnimation.builder()
                    .spawnTime(ParticleAnimation.SpawnTime.absolute(1))
                    .finalizes(RemoveParticleFinalizer.builder())
                    .spawn(RingSpawner
                            .noHeight()
                            .axis(Direction.Axis.Y)
                            .radius(.1f)
                            .rotPerTick(1)
                            .setTarget(PositionTarget.fixed(playerPos))
                            .setParticle(ParticleTypes.FLAME)
                    )
                    .terminatedWhen(TimedTerminator.ticks(600))
                    .then(MoveAwayElement.builder().speed(.01f).time(20).target(PositionTarget.fixed(playerPos)))
                    .sendToPlayer(player);
            return 1;
        });
    }

    private static int testAnimation(CommandContext<CommandSourceStack> context) {
        int index = IntegerArgumentType.getInteger(context, "testIndex");
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            Vec3 playerPos = player.position();
            if (index == 0) {
            } else if (index == 1) {
                ParticleAnimation.builder()
                        .spawnTime(ParticleAnimation.SpawnTime.absolute(1))
                        .finalizes(SetLifeTimeFinalizer.builder().resetAge().lifeTime(20))
                        .spawn(RingSpawner.entityWithBBSize(player, 1.7f, 1f)
                                .setParticle(ParticleTypes.FLAME)
                                .rotPerTick(5)
                                .heightPerTick(.02f)
                        )
                        .terminatedWhen(TimedTerminator.ticks(600))
                        .sendToPlayer(player);
            } else if (index == 2) {
            }
            return 1;
        });
    }
}
