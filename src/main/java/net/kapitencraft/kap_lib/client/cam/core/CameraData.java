package net.kapitencraft.kap_lib.client.cam.core;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CameraData {
    /**
     * position of the camera.
     */
    public Vec3 pos,
    /**
     * rotation of the camera. z is roll
     */
    rot;
    /**
     * old position and rotation. used for interpolation between ticks
     */
    private Vec3 oPos, oRot;
    /**
     * the target entity the camera should follow
     */
    public Entity target;
    /**
     * whether the camera is in first person
     */
    public boolean detached;
    /**
     * whether the perspective is 2nd or 3rd person
     */
    public boolean thirdPerson;

    public void update(Vec3 pos, Vec2 rot) {
        this.pos = pos;
        this.rot = MathHelper.withRoll(rot, 0);
        this.tick();
        Minecraft minecraft = Minecraft.getInstance();
        this.target = minecraft.getCameraEntity() == null ? minecraft.player : minecraft.getCameraEntity();
        CameraType cameraType = minecraft.options.getCameraType();
        this.detached = !cameraType.isFirstPerson();
        this.thirdPerson = !cameraType.isMirrored();
    }

    public void tick() {
        this.oPos = pos;
        this.oRot = rot;
    }

    /**
     * gets the current position of the entity interpolated between the last and current tick
     * <br>for rendering reasons
     */
    public Vec3 getPos(double partialTick) {
        return oPos.lerp(pos, partialTick);
    }

    public Vec3 getRot(double partialTick) {
        return oRot.lerp(rot, partialTick);
    }
}
