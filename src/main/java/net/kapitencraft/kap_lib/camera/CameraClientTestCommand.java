package net.kapitencraft.kap_lib.camera;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.kapitencraft.kap_lib.camera.core.CameraController;
import net.kapitencraft.kap_lib.camera.core.TrackingShot;
import net.kapitencraft.kap_lib.camera.modifiers.GlideTowardsModifier;
import net.kapitencraft.kap_lib.core.client.util.pos_target.EntityPositionTarget;
import net.kapitencraft.kap_lib.core.client.util.pos_target.StaticPositionTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.player.Player;

public class CameraClientTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("client_test")
                .then(Commands.literal("shake").executes(CameraClientTestCommand::shakeNoArg)
                        .then(Commands.argument("intensity", FloatArgumentType.floatArg(0, 1)).executes(CameraClientTestCommand::shakeIntensity)
                                .then(Commands.argument("strength", FloatArgumentType.floatArg(0, 100)).executes(CameraClientTestCommand::shakeIntensityStrength)
                                        .then(Commands.argument("speed", FloatArgumentType.floatArg(.01f, 5f)).executes(CameraClientTestCommand::shakeAll))
                                )
                        )
                ).then(Commands.literal("cam").executes(CameraClientTestCommand::testCameraMotion))
        );
    }

    private static int testCameraMotion(CommandContext<CommandSourceStack> context) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return 0;
        CameraController.INSTANCE.activate(TrackingShot.builder()
                .addModifier(
                        new GlideTowardsModifier(new EntityPositionTarget(player.getUUID(), EntityAnchorArgument.Anchor.EYES), new StaticPositionTarget(player.getViewVector(0).scale(20).add(EntityAnchorArgument.Anchor.EYES.apply(player)))), 40)
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
        CameraController.INSTANCE.shake(intensity, strength, speed);
        return 1;
    }
}
