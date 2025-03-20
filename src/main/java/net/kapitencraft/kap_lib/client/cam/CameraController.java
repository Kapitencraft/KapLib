package net.kapitencraft.kap_lib.client.cam;


import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * controls camera loc and rotation overwrites
 */
public class CameraController {
    private final Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
    private boolean running = false;
    private Vec3 oRot, rot;
    private int ticks = 0;
    private TrackingShot shot;

    private boolean shaking = false;
    private float oShake, shake, shakeVal;
    private float shakeIntensity;
    private int shakTime = 0;

    public CameraController() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void activate(TrackingShot shot) {
        this.running = true;
        this.shot = shot;
    }

    public void shake(float intensity, float strength) {
        this.shaking = true;
        this.shakeIntensity = intensity;
        this.shakeVal = strength;
    }

    public void tick() {
        oRot = rot;
        this.rot = shot.tickRot(ticks++);
        if (shaking) {
            oShake = shake;
            this.shake = Mth.cos(shakTime) * shakeVal;
            shakeVal -= shakeIntensity;
            if (shakeVal <= 0) shaking = false;
        }
    }

    @SubscribeEvent
    public void onViewportComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        double partialTick = event.getPartialTick();
        event.setYaw((float) Mth.lerp(partialTick, oRot.x, rot.x));
        event.setPitch((float) Mth.lerp(partialTick, oRot.y, rot.y));
        event.setRoll((float) Mth.lerp(partialTick, oRot.z, rot.z));
        if (shaking) {
            Vec3 pos = event.getCamera().getPosition();
            event.getCamera().setPosition(pos.add(0, Mth.lerp((float) partialTick, oShake, shake), 0));
        }
    }
}
