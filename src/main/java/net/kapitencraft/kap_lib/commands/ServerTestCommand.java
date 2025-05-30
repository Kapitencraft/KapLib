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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

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
                )
        );
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
