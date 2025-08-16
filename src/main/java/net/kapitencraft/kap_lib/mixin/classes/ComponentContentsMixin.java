package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ComponentSerialization.class)
public interface ComponentContentsMixin {

    @ModifyVariable(method = "createCodec", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    private ComponentContents.Type<?>[] types(ComponentContents.Type<?>[] in) {
        return ExtraRegistries.COMPONENT_CONTENT_TYPES.stream().toArray(ComponentContents.Type[]::new);
    }
}
