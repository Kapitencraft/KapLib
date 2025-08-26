package net.kapitencraft.kap_lib.client.cam.core;

import com.google.common.base.Preconditions;
import net.kapitencraft.kap_lib.client.cam.modifiers.Modifier;
import net.kapitencraft.kap_lib.client.cam.modifiers.GroupModifier;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TrackingShotData {
    final boolean suppressShake;
    final Modifier[] modifiers;
    final int[] times;

    public TrackingShotData(boolean suppressShake, Modifier[] modifiers, int[] modifierTimes) {
        this.suppressShake = suppressShake;
        this.modifiers = modifiers;
        this.times = modifierTimes;
    }

    public boolean suppressesShake() {
        return suppressShake;
    }

    public void toNw(FriendlyByteBuf buf) {
        buf.writeBoolean(this.suppressShake);
        NetworkHelper.writeArray(buf, modifiers, Modifier::toNw);
        buf.writeVarIntArray(this.times);
    }

    public static TrackingShotData fromNw(FriendlyByteBuf buf) {
        return new TrackingShotData(
                buf.readBoolean(),
                NetworkHelper.readArray(buf, Modifier[]::new, Modifier::fromNw),
                buf.readVarIntArray()
        );
    }

    public static class Builder {
        private final List<Modifier> modifiers = new ArrayList<>();
        private final List<Integer> modifierTimes = new ArrayList<>();
        private boolean suppressShake = false;

        public Builder addModifier(@NotNull Modifier modifier, int duration) {
            Preconditions.checkNotNull(modifier, "detected null modifier!");
            modifiers.add(modifier);
            modifierTimes.add(duration);
            return this;
        }

        public Builder addRotAndPosModifier(@NotNull Modifier posModifier, @NotNull Modifier rotModifier, int duration) {
            return this.addGroupModifier(duration, rotModifier, posModifier);
        }

        public Builder addGroupModifier(int duration, Modifier... modifiers) {
            return this.addModifier(new GroupModifier(modifiers), duration);
        }

        public Builder suppressesShake() {
            suppressShake = true;
            return this;
        }

        public TrackingShotData toData() {
            return new TrackingShotData(
                    suppressShake,
                    modifiers.toArray(new Modifier[0]),
                    modifierTimes.stream().mapToInt(Integer::intValue).toArray()
            );
        }

        public TrackingShot build() {
            return new TrackingShot(toData());
        }
    }
}
