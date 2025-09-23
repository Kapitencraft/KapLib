package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.mixin.duck.ScaledClientMotionPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientboundSetEntityMotionPacket.class)
public class ClientboundSetEntityMotionPacketMixin implements ScaledClientMotionPacket {
    @Shadow private int xa;
    @Shadow private int ya;
    @Shadow private int za;
    @Unique
    private float deltaScale;

    @Inject(method = "<init>(ILnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void calculateScale(int pId, Vec3 pDeltaMovement, CallbackInfo ci, double d0, double d1, double d2, double d3) {
        this.deltaScale = MathHelper.getOversizeScale(pDeltaMovement, new Vec3(d1, d2, d3));
        this.xa = (int) (pDeltaMovement.x * deltaScale * 8000);
        this.ya = (int) (pDeltaMovement.y * deltaScale * 8000);
        this.za = (int) (pDeltaMovement.z * deltaScale * 8000);
    }

    @ModifyConstant(method = {"getXa", "getYa", "getZa"}, constant = @Constant(doubleValue = 8000d))
    private double addDeltaScale(double constant) {
        return constant / deltaScale;
    }


    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void readScaleFromNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        this.deltaScale = pBuffer.readFloat();
        this.xa |= (pBuffer.readShort() & 0xFFFF) << 16;
        this.ya |= (pBuffer.readShort() & 0xFFFF) << 16;
        this.za |= (pBuffer.readShort() & 0xFFFF) << 16;
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void addScaleToNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        pBuffer.writeFloat(deltaScale);
        pBuffer.writeShort(this.xa >> 16);
        pBuffer.writeShort(this.ya >> 16);
        pBuffer.writeShort(this.za >> 16);
    }

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeShort(I)Lnet/minecraft/network/FriendlyByteBuf;"))
    private FriendlyByteBuf clampValue(FriendlyByteBuf instance, int value) {
        return instance.writeShort(value & 0xFFFF);
        //TODO ensure the entire int instead of only the short
    }

    @ModifyVariable(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "STORE", ordinal = 0), name = {"xa", "ya", "za"})
    private int readClampValue(int id) {
        return id & 0xFFFF;
    }

    @Override
    public float getScale() {
        return deltaScale;
    }
}
