package net.kapitencraft.kap_lib.particle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kapitencraft.kap_lib.core.util.Color;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.kapitencraft.kap_lib.particle.custom.LightningParticleOptions;
import net.kapitencraft.kap_lib.particle.custom.ShimmerShieldParticleOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ParticleClientTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("client_test")
                .then(Commands.literal("particle")
                        .then(Commands.literal("shield")
                                .executes(ParticleClientTestCommand::spawnShieldParticle)
                        ).then(Commands.literal("lightning")
                                .executes(ParticleClientTestCommand::spawnLightningParticle)
                        )
                )
        );
    }

    private static int spawnShieldParticle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().getEntityOrException()
                .level()
                .addParticle(
                        new ShimmerShieldParticleOptions(100, 50, Minecraft.getInstance().player.getId(), 5, 10, 1000, Color.RED, Color.GREEN, .01f, UUID.randomUUID()),
                        true, 0, 0, 0, 0, 0, 0
                );
        CommandHelper.sendSuccess(context.getSource(), "success!");
        return 1;
    }

    private static int spawnLightningParticle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = context.getSource().getEntityOrException();
        //new LightningHolder(new Vec3(0, 0, -10), new Vec3(10, 0, 0), 100);
        Level level = entity.level();
        level.addParticle(
                new LightningParticleOptions(new Vec3(0, 0, -10), new Vec3(10, 0, 0), 5, 1000, 1.5f, .2f),
                true, 0, 0, 0, 0, 0, 0
        );
        level.addParticle(ParticleTypes.FLAME, 0, 0, -10, 0, 0, 0);
        level.addParticle(ParticleTypes.FLAME, 10, 0, 0, 0, 0, 0);
        return 1;
    }
}
