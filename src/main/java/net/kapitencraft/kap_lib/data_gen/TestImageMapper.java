package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.data_gen.abst.TextureProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestImageMapper extends TextureProvider {
    public TestImageMapper(ExistingFileHelper existingFileHelper, PackOutput output) {
        super(existingFileHelper, output);
    }

    @Override
    protected void createEntries() {
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), KapLibMod.res("block/spell_bell_bottom"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("block/bell_bottom")));
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), KapLibMod.res("block/spell_bell_side"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("block/bell_side")));
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), KapLibMod.res("block/spell_bell_top"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("block/bell_top")));
    }
}
