package net.kapitencraft.kap_lib.client.cam.core;

import com.google.common.base.Preconditions;
import net.kapitencraft.kap_lib.client.cam.modifiers.Modifier;
import net.kapitencraft.kap_lib.client.cam.modifiers.GroupModifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TrackingShotData {
    public static final StreamCodec<RegistryFriendlyByteBuf, TrackingShotData> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, TrackingShotData::suppressesShake,
            Modifier.CODEC.apply(ByteBufCodecs.list()), t -> t.modifiers,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), t -> t.times,
            TrackingShotData::new
    );
    final boolean suppressShake;
    final List<Modifier> modifiers;
    final List<Integer> times;

    public TrackingShotData(boolean suppressShake, List<Modifier> modifiers, List<Integer> modifierTimes) {
        this.suppressShake = suppressShake;
        this.modifiers = modifiers;
        this.times = modifierTimes;
    }

    public boolean suppressesShake() {
        return suppressShake;
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
            return this.addModifier(new GroupModifier(List.of(modifiers)), duration);
        }

        public Builder suppressesShake() {
            suppressShake = true;
            return this;
        }

        public TrackingShotData toData() {
            return new TrackingShotData(
                    suppressShake,
                    modifiers,
                    modifierTimes
            );
        }

        public TrackingShot build() {
            return new TrackingShot(toData());
        }
    }
}
