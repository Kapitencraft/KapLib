package net.kapitencraft.kap_lib.data_gen;

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
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), testRL("block/spell_bell_bottom"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("block/bell_bottom")));
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), testRL("block/spell_bell_side"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("block/bell_side")));
        register(ResourceLocation.withDefaultNamespace("item/echo_shard"), testRL("block/spell_bell_top"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("block/bell_top")));

        registerMaterial(testRL("item/tin_ingot"), testRL("tin"), this::registerGoldBlock);

        register(ResourceLocation.withDefaultNamespace("item/clock_00"), testRL("item/clock/pale"))
                .then(Pale.INSTANCE);
        register(ResourceLocation.withDefaultNamespace("item/clock_00"), testRL("item/clock/saturated"))
                .then(Saturate.INSTANCE);
        register(ResourceLocation.withDefaultNamespace("item/clock_00"), testRL("item/clock/x_flipped"))
                .then(FlipX.INSTANCE);
        register(ResourceLocation.withDefaultNamespace("item/clock_00"), testRL("item/clock/y_flipped"))
                .then(FlipY.INSTANCE);
        register(ResourceLocation.withDefaultNamespace("item/clock_00"), testRL("item/clock/inverted"))
                .then(Invert.INSTANCE);
        register(ResourceLocation.withDefaultNamespace("item/clock_00"), testRL("item/clock/blue_shade"))
                .then(RedShade.INSTANCE);
        register(ResourceLocation.withDefaultNamespace("item/clock_00"), testRL("item/clock/green_shade"))
                .then(GreenShade.INSTANCE);
        register(ResourceLocation.withDefaultNamespace("item/clock_00"), testRL("item/clock/red_shade"))
                .then(BlueShade.INSTANCE);
    }

    private static ResourceLocation testRL(String path) {
        return ResourceLocation.fromNamespaceAndPath("test", path);
    }
}
