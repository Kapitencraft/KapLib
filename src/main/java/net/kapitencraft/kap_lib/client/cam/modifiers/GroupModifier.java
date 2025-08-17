package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class GroupModifier implements Modifier {
    private final List<Modifier> modifiers;

    public GroupModifier(List<Modifier> modifiers) {
        if (modifiers.isEmpty()) throw new IndexOutOfBoundsException("group modifier must contain at least one element");
        this.modifiers = modifiers;
    }

    @Override
    public void modify(int tick, double percentage, CameraData data) {
        for (Modifier modifier : this.modifiers) {
            modifier.modify(tick, percentage, data);
        }
    }

    @Override
    public Modifier.Type<?> getType() {
        return CameraModifiers.ROT_AND_POS.get();
    }

    public static class Type implements Modifier.Type<GroupModifier> {
        private static final StreamCodec<RegistryFriendlyByteBuf, GroupModifier> STREAM_CODEC = Modifier.CODEC.apply(ByteBufCodecs.list()).map(GroupModifier::new, m -> m.modifiers);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, GroupModifier> codec() {
            return STREAM_CODEC;
        }
    }
}
