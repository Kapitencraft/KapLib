package net.kapitencraft.kap_lib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.client.particle.animation.ParticleAnimation;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.RemoveParticleFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.SetLifeTimeFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.modifiers.MoveAwayElement;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

public class ServerTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("animation").then(Commands.argument("testIndex", IntegerArgumentType.integer(0, 1)).executes(ServerTestCommand::testAnimation))));
    }

    private static int testAnimation(CommandContext<CommandSourceStack> context) {
        int index = IntegerArgumentType.getInteger(context, "testIndex");
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            Vec3 playerPos = player.position();
            if (index == 0) {
                ParticleAnimation.builder()
                        .minSpawnTickTime(1).maxSpawnTickTime(1)
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
            } else if (index == 1) {
                ParticleAnimation.builder()
                        .maxSpawnTickTime(1).minSpawnTickTime(1)
                        .finalizes(SetLifeTimeFinalizer.builder().resetAge().lifeTime(20))
                        .spawn(RingSpawner.entityWithBBSize(player, 1.7f, 1f)
                                .setParticle(ParticleTypes.FLAME)
                                .rotPerTick(5)
                                .heightPerTick(.02f)
                        )
                        .terminatedWhen(TimedTerminator.ticks(600))
                        .sendToPlayer(player);
                }
            return 1;
        });
    }
}
