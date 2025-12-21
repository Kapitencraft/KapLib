package net.kapitencraft.kap_lib.mixin.classes;

import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StringUtil.class)
public class StringUtilMixin {

    @ModifyConstant(method = "isAllowedChatCharacter", constant = @Constant(intValue = 167))
    private static int modify(int in) {
        return -1;
    }
}
