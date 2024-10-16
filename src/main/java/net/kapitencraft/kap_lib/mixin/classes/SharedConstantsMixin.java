package net.kapitencraft.kap_lib.mixin.classes;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {

    @ModifyConstant(method = "isAllowedChatCharacter")
    private static int modify(int in) {
        if (in == 167) return -1;
        return in;
    }
}
