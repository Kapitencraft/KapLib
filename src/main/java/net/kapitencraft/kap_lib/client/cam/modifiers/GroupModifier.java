package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.network.FriendlyByteBuf;

public class GroupModifier implements Modifier {
    private final Modifier[] modifiers;

    public GroupModifier(Modifier[] modifiers) {
        if (modifiers.length < 1) throw new IndexOutOfBoundsException("group modifier must contain at least one element");
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

        @Override
        public GroupModifier fromNetwork(FriendlyByteBuf buf) {
            return new GroupModifier(NetworkHelper.readArray(buf, Modifier[]::new, Modifier::fromNw));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, GroupModifier value) {
            NetworkHelper.writeArray(buf, value.modifiers, Modifier::toNw);
        }
    }
}
