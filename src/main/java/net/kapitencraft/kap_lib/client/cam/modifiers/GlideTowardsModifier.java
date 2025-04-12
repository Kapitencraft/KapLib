package net.kapitencraft.kap_lib.client.cam.modifiers;

import net.kapitencraft.kap_lib.client.cam.core.CameraData;
import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.registry.custom.CameraModifiers;
import net.minecraft.network.FriendlyByteBuf;

public class GlideTowardsModifier implements Modifier {
    private final PositionTarget origin, target;

    public GlideTowardsModifier(PositionTarget origin, PositionTarget target) {
        this.origin = origin;
        this.target = target;
    }


    @Override
    public void modify(int tick, double percentage, CameraData data) {
        data.pos = origin.get().lerp(target.get(), percentage);
        data.detached = true;
    }

    @Override
    public Type getType() {
        return CameraModifiers.GLIDE_TOWARDS.get();
    }

    public static class Type implements Modifier.Type<GlideTowardsModifier> {

        @Override
        public GlideTowardsModifier fromNetwork(FriendlyByteBuf buf) {
            return new GlideTowardsModifier(PositionTarget.fromNw(buf), PositionTarget.fromNw(buf));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, GlideTowardsModifier value) {
            value.origin.toNw(buf);
            value.target.toNw(buf);
        }
    }
}
