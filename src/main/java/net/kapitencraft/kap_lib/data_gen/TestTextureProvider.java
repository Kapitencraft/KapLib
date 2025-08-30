package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.data_gen.abst.TextureProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TestTextureProvider extends TextureProvider {
    public TestTextureProvider(ExistingFileHelper existingFileHelper, PackOutput output) {
        super(existingFileHelper, output);
    }

    @Override
    protected void createEntries() {
        registerOre(new ResourceLocation("item/echo_shard"), KapLibMod.res("echo"));
        registerTools(new ResourceLocation("item/echo_shard"), KapLibMod.res("echo"));
        registerDiamondArmor(new ResourceLocation("item/echo_shard"), KapLibMod.res("echo"));
        register(new ResourceLocation("item/glowstone_dust"), KapLibMod.res("item/inverted_glowstone_dust"))
                .then(Invert.create());
        register(new ResourceLocation("item/redstone"), KapLibMod.res("item/bluestone"))
                .then(RedShade.create()); //might not be working
    }
}
