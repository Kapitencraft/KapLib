package net.kapitencraft.kap_lib.mixin.classes;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.mixin.duck.IKapLibComponentContents;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaComponentContentTypes;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ComponentContents.class)
public interface ComponentContentsMixin extends IKapLibComponentContents {


    @Override
    default Codec<? extends ComponentContents> getCodec() {
        ComponentContents contents = (ComponentContents) this;
        if (contents instanceof LiteralContents) return VanillaComponentContentTypes.LITERAL.get();
        else if (contents instanceof KeybindContents) return VanillaComponentContentTypes.KEY_BIND.get();
        else if (contents instanceof NbtContents) return VanillaComponentContentTypes.NBT.get();
        else if (contents instanceof ScoreContents) return VanillaComponentContentTypes.SCORE.get();
        else if (contents instanceof SelectorContents) return VanillaComponentContentTypes.SELECTOR.get();
        else if (contents == ComponentContents.EMPTY) return VanillaComponentContentTypes.EMPTY.get();
        else return VanillaComponentContentTypes.TRANSLATABLE.get();
    }
}
