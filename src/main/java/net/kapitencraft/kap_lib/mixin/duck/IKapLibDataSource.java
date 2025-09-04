package net.kapitencraft.kap_lib.mixin.duck;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.contents.DataSource;

public interface IKapLibDataSource {

    MapCodec<? extends DataSource> getCodec();

    static MapCodec<? extends DataSource> codecFromVanilla(DataSource dataSource) {
        return ((IKapLibDataSource) dataSource).getCodec();
    }
}
