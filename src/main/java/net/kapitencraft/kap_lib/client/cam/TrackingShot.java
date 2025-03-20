package net.kapitencraft.kap_lib.client.cam;

import net.kapitencraft.kap_lib.client.cam.rot.Rotator;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class TrackingShot {
    private final Rotator[] rotators;
    //TODO implement times and setup rest

    public TrackingShot(Rotator[] rotators) {
        this.rotators = rotators;
    }

    public void toNw(FriendlyByteBuf buf) {
        NetworkHelper.writeArray(buf, rotators, Rotator::toNw);
    }

    public static TrackingShot fromNw(FriendlyByteBuf buf) {
        return new TrackingShot(NetworkHelper.readArray(buf, Rotator[]::new, Rotator::fromNw));
    }

    public Vec3 tickRot(int i) {
        return null;
    }
}
