package net.kapitencraft.kap_lib.mixin.classes.client;

import net.kapitencraft.kap_lib.mixin.duck.ScaledClientMotionPacket;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @ModifyConstant(method = "handleSetEntityMotion", constant = @Constant(doubleValue = 8000d))
    private double addDeltaScale(double constant, ClientboundSetEntityMotionPacket packet) {
        float scale = ScaledClientMotionPacket.get(packet).getScale();
        return constant * scale;
    }
}
