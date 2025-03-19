package net.kapitencraft.kap_lib.client.cam;


import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.sound.midi.Track;

/**
 * controls camera loc and rotation overwrites
 */
public class CameraController {
    private final Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
    private boolean running = false;
    private Vec3 oRot, rot;
    private int ticks = 0;
    private TrackingShot shot;

    public CameraController() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void activate(TrackingShot shot) {
        this.running = true;
        this.shot = shot;
    }

    public void tick() {
        oRot = rot;
        this.rot = shot.tickRot(ticks++);
    }

    @SubscribeEvent
    public void onViewportComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        double partialTick = event.getPartialTick();
        event.setYaw((float) Mth.lerp(partialTick, oRot.x, rot.x));
        event.setPitch((float) Mth.lerp(partialTick, oRot.y, rot.y));
        event.setRoll((float) Mth.lerp(partialTick, oRot.z, rot.z));
    }
}
