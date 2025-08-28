package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.data_gen.abst.ImagePaletteMapper;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TestImageMapper extends ImagePaletteMapper {
    public TestImageMapper(ExistingFileHelper existingFileHelper, PackOutput output) {
        super(existingFileHelper, output);
    }

    @Override
    protected void createEntries() {
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), ResourceLocation.withDefaultNamespace("item/gold_ingot"), KapLibMod.res("item/echo_ingot"));
    }
}
