package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.mixin.duck.ScaledClientMotionPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
    private void calculateScale(int pId, Vec3 pDeltaMovement, CallbackInfo ci, double d0, double d1, double d2) {
        this.deltaScale = MathHelper.getOversizeScale(pDeltaMovement, new Vec3(d0, d1, d2));
        this.xa = (int) (pDeltaMovement.x * deltaScale * 8000);
        this.ya = (int) (pDeltaMovement.y * deltaScale * 8000);
        this.za = (int) (pDeltaMovement.z * deltaScale * 8000);
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void readScaleFromNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        this.deltaScale = pBuffer.readFloat();
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void addScaleToNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        pBuffer.writeFloat(deltaScale);
    }

    @Override
    public float getScale() {
        return deltaScale;
    }
}
