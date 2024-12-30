package net.kapitencraft.kap_lib.helpers;

import net.kapitencraft.kap_lib.util.particle_help.ParticleAmountHolder;
import net.kapitencraft.kap_lib.util.particle_help.ParticleGradientHolder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ParticleHelper {

    public static <T extends ParticleOptions> int sendParticles(Level level, T type, boolean force, double x, double y, double z, int amount, double deltaX, double deltaY, double deltaZ, double speed) {
        if (level instanceof ServerLevel serverLevel) {
            List<ServerPlayer> players = serverLevel.getPlayers(serverPlayer -> true);
            int i;
            for (i = 0; i < players.size(); i++) {
                ServerPlayer serverPlayer = players.get(i);
                serverLevel.sendParticles(serverPlayer, type, force, x, y, z, amount, deltaX, deltaY, deltaZ, speed);
            }
            return i;
        }
        return -1;
    }

    public static <T extends ParticleOptions> int sendParticles(T type, boolean force, Entity source, int amount, double deltaX, double deltaY, double deltaZ, double speed) {
        return sendParticles(source.level(), type, force, source.position(), amount, deltaX, deltaY, deltaZ, speed);
    }
    public static <T extends ParticleOptions> int sendParticles(Level level, T type, boolean force, Vec3 loc, int amount, double deltaX, double deltaY, double deltaZ, double speed) {
        return sendParticles(level, type, force, loc.x, loc.y, loc.z, amount, deltaX, deltaY, deltaZ, speed);
    }

    public static <T extends ParticleOptions> int sendParticles(Level level, boolean force, Vec3 loc, double deltaX, double deltaY, double deltaZ, double speed, ParticleAmountHolder holder) {
        return sendParticles(level, holder.particleType(), force, loc.x, loc.y, loc.z, holder.amount(), deltaX, deltaY, deltaZ, speed);
    }

    public static <T extends ParticleOptions> int sendParticles(Level level, boolean force, Vec3 loc, double deltaX, double deltaY, double deltaZ, double speed, ParticleGradientHolder holder) {
        return sendParticles(level, force, loc, deltaX, deltaY, deltaZ, speed, holder.holder1()) + sendParticles(level, force, loc, deltaX, deltaY, deltaZ, speed, holder.holder2());
    }

    public static void sendAlwaysVisibleParticles(ParticleOptions type, Level level, double x, double y, double z, double dx, double dy, double dz, double sx, double sy, double sz, int amount) {
        for (int i = 0; i < amount; i++) {
            double xOff = (Math.random() * 2 * dx) - dx;
            double yOff = (Math.random() * 2 * dy) - dy;
            double zOff = (Math.random() * 2 * dz) - dz;
            level.addAlwaysVisibleParticle(type, true, x + xOff, y + yOff, z + zOff, sx, sy, sz);
        }
    }
}
