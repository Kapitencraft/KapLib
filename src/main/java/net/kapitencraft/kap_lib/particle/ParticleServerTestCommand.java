package net.kapitencraft.kap_lib.particle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.core.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.core.client.util.rot_target.RotationTarget;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.kapitencraft.kap_lib.particle.animation.AnimationUtils;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.elements.KeepAliveElement;
import net.kapitencraft.kap_lib.particle.animation.elements.MoveAwayElement;
import net.kapitencraft.kap_lib.particle.animation.elements.RotateElement;
import net.kapitencraft.kap_lib.particle.animation.finalizers.RemoveParticleFinalizer;
import net.kapitencraft.kap_lib.particle.animation.finalizers.SetLifeTimeFinalizer;
import net.kapitencraft.kap_lib.particle.animation.spawners.GroupSpawner;
import net.kapitencraft.kap_lib.particle.animation.spawners.LineSpawner;
import net.kapitencraft.kap_lib.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.particle.animation.spawners.SingleSpawner;
import net.kapitencraft.kap_lib.particle.animation.terminators.TimedTerminator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;

/**
 * server tests.
 */
public class ParticleServerTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("animation")
                        .then(Commands.literal("rotation")
                                .executes(ParticleServerTestCommand::testRotation)
                        ).then(Commands.literal("arrow")
                                .executes(ParticleServerTestCommand::testArrow)
                        ).then(Commands.literal("aura")
                                .executes(ParticleServerTestCommand::testAura)
                        ).then(Commands.literal("star")
                                .executes(ParticleServerTestCommand::testStar)
                        ).then(Commands.literal("line")
                                .executes(ParticleServerTestCommand::testLine)
                        ).executes(ParticleServerTestCommand::testAnimation)
                )
        );
    }

    private static int testStar(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            PositionTarget center = PositionTarget.fixed(player.position());
            AnimationUtils.star(5, ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.FLAME, .25f, 5f, center)
                    .terminatedWhen(TimedTerminator.ticks(600))
                    .finalizes(RemoveParticleFinalizer.builder())
                    //.then(KeepAliveElement.forDuration(200))
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
            Arrow arrow = new Arrow(EntityType.ARROW, player.level());
            arrow.setPos(player.position());
            arrow.setXRot(player.getXRot());
            arrow.setYRot(player.getYRot());
            arrow.setNoGravity(true);
            player.level().addFreshEntity(arrow);
            ParticleAnimation.requireEntity(arrow)
                    .spawnTime(ParticleAnimation.SpawnTime.absolute(1))
                    .finalizes(SetLifeTimeFinalizer.builder().resetAge().lifeTime(20))
                    .spawn(GroupSpawner.builder()
                            .addSpawner(testWithColor(arrow, 0x0000FF).axis(Direction.Axis.Z))
                            .addSpawner(testWithColor(arrow, 0x00FF00).axis(Direction.Axis.Y))
                            .addSpawner(testWithColor(arrow, 0xFF0000).axis(Direction.Axis.X))
                    )
                    .sendToPlayer(player);
            return 1;
        });
    }

    private static RingSpawner.Builder testWithColor(Entity entity, int colorPacked) {
        return RingSpawner.noHeight()
                .setTarget(PositionTarget.entity(entity))
                .rotation(RotationTarget.forEntity(entity))
                .spawnCount(2)
                .rotPerTick(5)
                .radius(.3f)
                .setParticle(new DustParticleOptions(Vec3.fromRGB24(colorPacked).toVector3f(), .34f));
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
                    ).terminatedWhen(TimedTerminator.ticks(600))
                    .then(MoveAwayElement.builder().speed(.01f).time(20).target(PositionTarget.fixed(playerPos)))
                    .sendToPlayer(player);
            return 1;
        });
    }

    private static int testAnimation(CommandContext<CommandSourceStack> context) {
        return CommandHelper.checkNonConsoleCommand(context, (player, commandSourceStack) -> {
            Vec3 playerPos = player.position().add(0, -2, 0);
            ParticleAnimation.builder()
                    .spawnTime(ParticleAnimation.SpawnTime.once())
                    .finalizes(RemoveParticleFinalizer.builder())
                    .spawn(SingleSpawner.at(ParticleTypes.FLAME, PositionTarget.fixed(playerPos.add(5, 0, 0))))
                    .then(RotateElement.builder()
                            .angle(1)
                            .duration(360)
                            .axis(Direction.Axis.Y)
                            .pivot(PositionTarget.fixed(playerPos))
                    )
                    .terminatedWhen(TimedTerminator.seconds(20))
                    .sendToPlayer(player);
            return 1;
        });
    }
}
