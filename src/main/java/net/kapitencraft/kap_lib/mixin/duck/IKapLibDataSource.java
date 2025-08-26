package net.kapitencraft.kap_lib.mixin.duck;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.contents.DataSource;

public interface IKapLibDataSource {

    Codec<? extends DataSource> getCodec();

    static Codec<? extends DataSource> codecFromVanilla(DataSource dataSource) {
        return ((IKapLibDataSource) dataSource).getCodec();
    }
}
