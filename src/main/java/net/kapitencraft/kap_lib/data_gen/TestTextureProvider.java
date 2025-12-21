package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.datagen.TextureProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestTextureProvider extends TextureProvider {
    public TestTextureProvider(ExistingFileHelper existingFileHelper, PackOutput output) {
        super(existingFileHelper, output);
    }

    @Override
    protected void createEntries() {
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), LibConstants.res("block/spell_bell_bottom"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("block/bell_bottom")));
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), LibConstants.res("block/spell_bell_side"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("block/bell_side")));
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), LibConstants.res("block/spell_bell_top"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("block/bell_top")));
    }
}
