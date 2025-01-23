package net.kapitencraft.kap_lib.mixin.duck;

import net.kapitencraft.kap_lib.mixin.classes.ClientboundSetEntityMotionPacketMixin;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

/**
 * what is this???
 */
public interface ScaledClientMotionPacket {

    float getScale();

    static ScaledClientMotionPacket get(ClientboundSetEntityMotionPacket packet) {
        return (ScaledClientMotionPacket) packet;
    }
}
