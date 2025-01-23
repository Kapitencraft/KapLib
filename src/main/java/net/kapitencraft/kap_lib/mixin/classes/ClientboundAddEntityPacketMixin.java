package net.kapitencraft.kap_lib.mixin.classes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientboundAddEntityPacket.class)
public class ClientboundAddEntityPacketMixin {
    @Shadow
    private byte xRot;
    @Shadow
    private byte yRot;
    @Unique
    private byte xRot2, yRot2;

    @Unique
    private short createEncodedY(float in) {
        return (short) (in  / 360 * 65535);
    }

    @Unique
    private short packX() {
        return (short) ((xRot & 255) << 8 | (xRot2 & 255));
    }

    @Unique
    private short packY() {
        return (short) ((yRot & 255) << 8 | (yRot2 & 255));
    }

    @Unique
    private short createEncodedX(float in) {
        return (short) (in / 180 * 65535);
    }

    @Inject(method = "<init>(ILjava/util/UUID;DDDFFLnet/minecraft/world/entity/EntityType;ILnet/minecraft/world/phys/Vec3;D)V", at = @At("TAIL"))
    private void add2Byte(int pId, UUID pUuid, double pX, double pY, double pZ, float pXRot, float pYRot, EntityType<?> pType, int pData, Vec3 pDeltaMovement, double pYHeadRot, CallbackInfo ci) {
        short xRotData = createEncodedX(pXRot);
        xRot = (byte) (xRotData >> 8);
        xRot2 = (byte) (xRotData & 255);
        short yRotData = createEncodedY(pYRot);
        yRot = (byte) (yRotData >> 8);
        yRot2 = (byte) (yRotData & 255);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void addExtraByteToNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        pBuffer.writeByte(xRot2);
        pBuffer.writeByte(yRot2);
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void readExtraByteFromNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        this.xRot2 = pBuffer.readByte();
        this.yRot2 = pBuffer.readByte();
    }

    /**
     * @author Kapitencraft
     * @reason precision fix
     */
    @Overwrite
    public float getYRot() {
        return ((float) packY()) / 65535 * 360;
    }

    /**
     * @author Kapitencraft
     * @reason precision fix
     */
    @Overwrite
    public float getXRot() {
        return ((float) packX()) / 65535 * 180;
    }
}
