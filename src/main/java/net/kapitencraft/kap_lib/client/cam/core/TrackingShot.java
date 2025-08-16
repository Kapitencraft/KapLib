package net.kapitencraft.kap_lib.client.cam.core;

import net.kapitencraft.kap_lib.client.cam.modifiers.Modifier;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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
        this.modifier = data.modifiers.get(0);
    }

    public void tick(CameraData camData) {
        try {
            int deltaTickTime = tickCount - posStartTick;
            modifier.modify(deltaTickTime, (double) deltaTickTime / (data.times.get(posStartTick) - 1), camData);

            if (deltaTickTime >= data.times.get(index)) {
                if (++index < data.modifiers.size()) {
                    modifier = data.modifiers.get(index);
                    posStartTick = tickCount;
                }
            }
            tickCount++;
        } catch (Exception e) {

            //if (true) throw e;
            CrashReport report = new CrashReport("Ticking TrackingShot", e);
            report.addCategory("Tracking Shot Data")
                    .setDetail("ModifierData", data.modifiers)
                    .setDetail("ModifierTimes", data.times)
                    .setDetail("Disable Shake", data.suppressShake);
            throw new ReportedException(report);
        }
    }

    boolean suppressesShake() {
        return data.suppressesShake();
    }

    public boolean done() {
        return index == data.modifiers.size();
    }
}
