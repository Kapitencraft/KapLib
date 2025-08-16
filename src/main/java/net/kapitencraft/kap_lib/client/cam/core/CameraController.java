package net.kapitencraft.kap_lib.client.cam.core;


import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * controls camera loc and rotation overwrites
 */
@OnlyIn(Dist.CLIENT)
public class CameraController {
    private final Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
    private boolean running = false, wasHiding = false;
    private TrackingShot shot;

    public final CameraData data;

    private boolean shaking = false;
    private float oShake, shake, shakeVal;
    private float shakeIntensity, shakeFrequency;
    private int shakeTime = 0;

    public CameraController() {
        this.data = new CameraData();
        NeoForge.EVENT_BUS.register(this);
    }

    public void activate(TrackingShot shot) {
        this.running = true;
        this.shot = shot;
        shot.setup();
        Vec3 pos = camera.getPosition();
        Vec2 rot = new Vec2(camera.getXRot(), camera.getYRot());
        this.data.update(pos, rot);
        Options options = Minecraft.getInstance().options;
        wasHiding = options.hideGui;
        options.hideGui = true;
    }

    public void shake(float intensity, float strength, float frequency) {
        this.shaking = true;
        this.shakeIntensity = intensity;
        this.shakeVal = strength;
        this.shakeFrequency = frequency;
    }

    @SubscribeEvent
    public void tick(ClientTickEvent.Pre event) {
        if (running) {
            if (shot.done()) disable();
            else {
                data.tick();
                shot.tick(data);
            }
        }
        if (shaking) {
            oShake = shake;

            this.shake = Mth.sin(shakeTime++ * shakeFrequency) * shakeVal;
            shakeVal -= shakeIntensity;
            if (shakeVal <= 0) shaking = false;
        }
    }

    private void disable() {
        this.running = false;
        Minecraft.getInstance().options.hideGui = wasHiding;
    }

    @SubscribeEvent
    public void onViewportComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        double partialTick = event.getPartialTick();
        if (running) {
            Vec3 rot = data.getRot(partialTick);
            event.setPitch((float) rot.x);
            event.setYaw((float) rot.y);
            event.setRoll((float) rot.z);

            event.getCamera().setPosition(data.getPos(partialTick));
        }
        if (shaking && (!running || !shot.suppressesShake())) {
            Vec3 pos = event.getCamera().getPosition();
            event.getCamera().setPosition(pos.add(0, Mth.lerp((float) partialTick, oShake, shake), 0));
        }
    }

    public boolean running() {
        return running;
    }
}
