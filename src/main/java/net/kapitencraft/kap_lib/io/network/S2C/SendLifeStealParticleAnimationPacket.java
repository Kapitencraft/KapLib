package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SendLifeStealParticleAnimationPacket implements SimplePacket {
    private final ParticleAnimation animation;

    public SendLifeStealParticleAnimationPacket(ParticleAnimation animation) {
        this.animation = animation;
    }

    public SendLifeStealParticleAnimationPacket(FriendlyByteBuf buf) {
        this(ParticleAnimation.fromNw(buf));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        animation.toNW(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            if (ClientModConfig.lifeStealParticleEnabled()) {
                LibClient.animations.accept(animation);
            }
        });
    }
}
