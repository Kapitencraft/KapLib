package net.kapitencraft.kap_lib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.EntityAddedTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.RemoveParticleFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.SetLifeTimeFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.modifiers.MoveAwayElement;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.RingSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.EntityRemovedTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.CommandHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;

public class ServerTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("server_test")
                .then(Commands.literal("animation").then(Commands.argument("testIndex", IntegerArgumentType.integer(0, 2)).executes(ServerTestCommand::testAnimation))));
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
            } else if (index == 2) {
                Arrow arrow = new Arrow(player.level(), 0, 0, 0);
                arrow.shootFromRotation(player, (float) player.getX(), (float) player.getY(), (float) player.getZ(), 0.5f, 0f);
                player.level().addFreshEntity(arrow);
                ParticleAnimation.builder()
                        .maxSpawnTickTime(1).minSpawnTickTime(1)
                        .finalizes(SetLifeTimeFinalizer.builder().resetAge().lifeTime(20))
                        .spawn(RingSpawner.noHeight()
                                .setTarget(PositionTarget.entity(arrow))
                                .spawnCount(2)
                                .rotPerTick(5)
                                .axis(Direction.Axis.Z)
                                .radius(.3f)
                                .setParticle(new DustParticleOptions(Vec3.fromRGB24(0xFFFFFF).toVector3f(), .34f))
                        )
                        .terminatedWhen(EntityRemovedTerminator.builder(arrow))
                        .activatedOn(EntityAddedTrigger.forEntity(arrow))
                        .sendToPlayer(player);
            }
            return 1;
        });
    }
}
