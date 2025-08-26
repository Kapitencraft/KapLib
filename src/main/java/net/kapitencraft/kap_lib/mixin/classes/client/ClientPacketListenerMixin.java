package net.kapitencraft.kap_lib.mixin.classes.client;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.kapitencraft.kap_lib.mixin.duck.ScaledClientMotionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @ModifyConstant(method = "handleSetEntityMotion", constant = @Constant(doubleValue = 8000d))
    private double addDeltaScale(double constant, ClientboundSetEntityMotionPacket packet) {
        float scale = ScaledClientMotionPacket.get(packet).getScale();
        return constant * scale;
    }
}
