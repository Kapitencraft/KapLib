package net.kapitencraft.kap_lib.core.mixin.classes;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientboundSetEntityMotionPacket.class)
public class ClientboundSetEntityMotionPacketMixin {
    @Shadow
    private int xa;
    @Shadow
    private int ya;
    @Shadow
    private int za;
    @Unique
    private boolean precise;
    @Unique
    private int xa1, ya1, za1;

    @Inject(method = "<init>(ILnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
    private void calculateScale(int pId, Vec3 pDeltaMovement, CallbackInfo ci, @Local(ordinal = 1) double d1, @Local(ordinal = 2) double d2, @Local(ordinal = 3) double d3) {
        //check if any dimension of the motion surpasses the clamped length of 3.9
        precise = pDeltaMovement.x != d1 || pDeltaMovement.y != d2 || pDeltaMovement.z != d3;
        if (precise) {
            long x = Double.doubleToLongBits(pDeltaMovement.x);
            xa = (int) (x >> 32);
            xa1 = (int) x;
            long y = Double.doubleToLongBits(pDeltaMovement.y);
            ya = (int) (y >> 32);
            ya1 = (int) y;
            long z = Double.doubleToLongBits(pDeltaMovement.z);
            za = (int) (z >> 32);
            za1 = (int) z;
        }
    }

    @Inject(method = "getXa", at = @At("HEAD"), cancellable = true)
    private void overrideXIfPrecise(CallbackInfoReturnable<Double> cir) {
        if (this.precise)
            cir.setReturnValue(Double.longBitsToDouble(((long) xa << 32) | ((long) xa1 & 0xFFFFFFFFL)));
    }

    @Inject(method = "getYa", at = @At("HEAD"), cancellable = true)
    private void overrideYIfPrecise(CallbackInfoReturnable<Double> cir) {
        if (this.precise)
            cir.setReturnValue(Double.longBitsToDouble(((long) ya << 32) | ((long) ya1 & 0xFFFFFFFFL)));
    }

    @Inject(method = "getZa", at = @At("HEAD"), cancellable = true)
    private void overrideZIfPrecise(CallbackInfoReturnable<Double> cir) {
        if (this.precise)
            cir.setReturnValue(Double.longBitsToDouble(((long) za << 32) | ((long) za1 & 0xFFFFFFFFL)));
    }

    @SuppressWarnings("AssignmentUsedAsCondition")
    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void readScaleFromNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        if (this.precise = pBuffer.readBoolean()) {
            this.xa = (this.xa & 0xFFFF) | pBuffer.readShort() << 16;
            this.ya = (this.ya & 0xFFFF) | pBuffer.readShort() << 16;
            this.za = (this.za & 0xFFFF) | pBuffer.readShort() << 16;
            this.xa1 = pBuffer.readInt();
            this.ya1 = pBuffer.readInt();
            this.za1 = pBuffer.readInt();
        }
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void addScaleToNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        pBuffer.writeBoolean(this.precise);
        if (this.precise) {
            pBuffer.writeShort(this.xa >> 16);
            pBuffer.writeShort(this.ya >> 16);
            pBuffer.writeShort(this.za >> 16);
            pBuffer.writeInt(this.xa1);
            pBuffer.writeInt(this.ya1);
            pBuffer.writeInt(this.za1);
        }
    }
}
