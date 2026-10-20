package net.kapitencraft.kap_lib.component.mixin.classes;

import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StringUtil.class)
public class StringUtilMixin {

    @ModifyConstant(method = "isAllowedChatCharacter")
    private static int modify(int in) {
        if (in == 167) return -1;
        return in;
    }
}
