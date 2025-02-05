package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SendParticleAnimationPacket implements SimplePacket {
    private final ParticleAnimation animation;

    public SendParticleAnimationPacket(ParticleAnimation animation) {
        this.animation = animation;
    }

    public SendParticleAnimationPacket(FriendlyByteBuf buf) {
        this(ParticleAnimation.fromNw(buf));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        animation.toNW(buf);
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> LibClient.animations.accept(animation));
        return true;
    }
}
