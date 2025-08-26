package net.kapitencraft.kap_lib.mixin.duck;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.ComponentContents;

public interface IKapLibComponentContents {

    Codec<? extends ComponentContents> getCodec();

    static Codec<? extends ComponentContents> codecFromVanilla(ComponentContents componentContents) {
        return ((IKapLibComponentContents) componentContents).getCodec();
    }
}
