package net.kapitencraft.kap_lib.mixin.classes;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.mixin.duck.IKapLibDataSource;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaDataSourceTypes;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.EntityDataSource;
import net.minecraft.network.chat.contents.StorageDataSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DataSource.class)
public interface DataSourceMixin extends IKapLibDataSource {

    @Override
    default Codec<? extends DataSource> getCodec() {
        DataSource dataSource = (DataSource) this;
        if (dataSource instanceof EntityDataSource) return VanillaDataSourceTypes.ENTITY.get();
        else if (dataSource instanceof StorageDataSource) return VanillaDataSourceTypes.STORAGE.get();
        else return VanillaDataSourceTypes.BLOCK.get();
    }
}
