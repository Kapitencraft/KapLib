package net.kapitencraft.kap_lib.camera;

import net.kapitencraft.kap_lib.camera.network.S2C.ActivateShakePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class CameraUtils {

    public static void shakeGround(ServerLevel level, Vec3 pos, float intensity, float strength, float frequency) {
        float radius = strength / intensity;
        List<ServerPlayer> targets = level.getEntitiesOfClass(ServerPlayer.class, new AABB(pos, pos).inflate(radius));
        targets.forEach(p -> {
            float dist = Mth.sqrt((float) p.distanceToSqr(pos));
            PacketDistributor.sendToPlayer(p, new ActivateShakePacket(intensity, strength * (dist / radius), frequency));
        });
    }
}
