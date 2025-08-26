package net.kapitencraft.kap_lib.client.cam.core;

import net.kapitencraft.kap_lib.client.cam.modifiers.Modifier;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

@OnlyIn(Dist.CLIENT)
public class TrackingShot {
    private final TrackingShotData data;
    private Modifier modifier;
    private int tickCount;
    private int rotIndex, index;
    private int rotStartTick, posStartTick;

    public TrackingShot(TrackingShotData data) {
        this.data = data;
    }

    public static TrackingShotData.Builder builder() {
        return new TrackingShotData.Builder();
    }

    public void setup() {
        this.modifier = data.modifiers[0];
    }

    public void tick(CameraData camData) {
        try {
            int deltaTickTime = tickCount - posStartTick;
            modifier.modify(deltaTickTime, (double) deltaTickTime / (data.times[posStartTick] - 1), camData);

            if (deltaTickTime >= data.times[index]) {
                if (++index < data.modifiers.length) {
                    modifier = data.modifiers[index];
                    posStartTick = tickCount;
                }
            }
            tickCount++;
        } catch (Exception e) {

            //if (true) throw e;
            CrashReport report = new CrashReport("Ticking TrackingShot", e);
            report.addCategory("Tracking Shot Data")
                    .setDetail("ModifierData", Arrays.toString(data.modifiers))
                    .setDetail("ModifierTimes", Arrays.toString(data.times))
                    .setDetail("Disable Shake", data.suppressShake);
            throw new ReportedException(report);
        }
    }

    boolean suppressesShake() {
        return data.suppressesShake();
    }

    public boolean done() {
        return index == data.modifiers.length;
    }
}
