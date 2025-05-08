package net.kapitencraft.kap_lib.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.cam.core.TrackingShot;
import net.kapitencraft.kap_lib.client.cam.modifiers.GlideTowardsModifier;
import net.kapitencraft.kap_lib.client.gui.screen.TestScreen;
import net.kapitencraft.kap_lib.client.particle.LightningParticleOptions;
import net.kapitencraft.kap_lib.client.particle.ShimmerShieldParticleOptions;
import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.CommandHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.GlyphEffects;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ClientTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("client_test")
                    .then(Commands.literal("screen")
                        .executes(ClientHelper.createScreenCommand(TestScreen::new))
                ).then(Commands.literal("particle")
                        .then(Commands.literal("shield")
                                .executes(ClientTestCommand::spawnShieldParticle)
                        ).then(Commands.literal("lightning")
                                .executes(ClientTestCommand::spawnLightningParticle)
                        )
                ).then(Commands.literal("chroma")
                        .executes(ClientTestCommand::testChroma)
                ).then(Commands.literal("shake").executes(ClientTestCommand::shakeNoArg)
                        .then(Commands.argument("intensity", FloatArgumentType.floatArg(0, 1)).executes(ClientTestCommand::shakeIntensity)
                                .then(Commands.argument("strength", FloatArgumentType.floatArg(0, 100)).executes(ClientTestCommand::shakeIntensityStrength)
                                        .then(Commands.argument("speed", FloatArgumentType.floatArg(.01f, 5f)).executes(ClientTestCommand::shakeAll))
                                )
                        )
                ).then(Commands.literal("cam").executes(ClientTestCommand::testCameraMotion))
        );
    }

    private static int testCameraMotion(CommandContext<CommandSourceStack> context) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0;
        LibClient.cameraControl.activate(TrackingShot.builder()
                .addModifier(
                        new GlideTowardsModifier(PositionTarget.entityEyes(player), PositionTarget.fixed(player.getViewVector(0).scale(20).add(EntityAnchorArgument.Anchor.EYES.apply(player)))), 40)
                .build()
        );
        return 1;
    }

    private static int shakeAll(CommandContext<CommandSourceStack> context) {
        return shake(
                FloatArgumentType.getFloat(context, "intensity"),
                FloatArgumentType.getFloat(context, "strength"),
                FloatArgumentType.getFloat(context, "speed")
        );
    }

    private static int shakeIntensityStrength(CommandContext<CommandSourceStack> context) {
        return shake(FloatArgumentType.getFloat(context, "intensity"), FloatArgumentType.getFloat(context, "strength"), 1.5f);
    }

    private static int shakeIntensity(CommandContext<CommandSourceStack> context) {
        return shake(FloatArgumentType.getFloat(context, "intensity"), .5f, 1.5f);
    }

    private static int shakeNoArg(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        return shake(.01f, .5f, 1.5f);
    }

    private static int shake(float intensity, float strength, float speed) {
        LibClient.cameraControl.shake(intensity, strength, speed);
        return 1;
    }

    private static int testChroma(CommandContext<CommandSourceStack> commandContext) {
        for (int i = 0; i < 10; i++) commandContext.getSource().sendSystemMessage(Component.literal("EEEEEEEEEEEEEEEEEE").setStyle(MiscHelper.withSpecial(Style.EMPTY, GlyphEffects.RAINBOW)));
        return 1;
    }

    private static int spawnShieldParticle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        context.getSource().getEntityOrException()
                .level()
                .addParticle(
                        new ShimmerShieldParticleOptions(100, 50, Minecraft.getInstance().player.getId(), 5, 10, 1000, new Color(0xFFFF0000), new Color(0xFF00FF00), .01f, UUID.randomUUID()),
                        true, 0, 0, 0, 0, 0, 0
                );
        CommandHelper.sendSuccess(context.getSource(), "success!");
        return 1;
    }


    private static int spawnLightningParticle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity entity = context.getSource().getEntityOrException();
        entity.level()
                .addParticle(
                        new LightningParticleOptions(entity.position(), entity.getViewVector(0).scale(20).add(entity.position())),
                        true, 0, 0, 0, 0, 0, 0
                );
        return 1;
    }
}
