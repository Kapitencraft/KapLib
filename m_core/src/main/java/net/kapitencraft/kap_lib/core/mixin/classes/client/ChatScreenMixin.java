package net.kapitencraft.kap_lib.core.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.core.helpers.CommandHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 1))
    public void setScreen(Minecraft instance, Screen old, Operation<Void> original) {
        if (CommandHelper.postCommandScreen != null) {
            original.call(instance, CommandHelper.postCommandScreen);
            CommandHelper.postCommandScreen = null;
        } else original.call(instance, null);
    }
}