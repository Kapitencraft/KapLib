package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.data_gen.tags.ModTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class Generator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        registries = generator.addProvider(true, new ModRegistriesProvider(output, registries)).getRegistryProvider();
        generator.addProvider(true, new ExtraNumbersLangProvider(output));
        CompletableFuture<TagsProvider.TagLookup<Block>> blockTagLookup = generator.addProvider(true, new ModTagsProvider.Block(output, registries, helper)).contentsGetter();
        generator.addProvider(true, new TestItemRequirements(output));
        generator.addProvider(true, new TestBlockRequirements(output));
        generator.addProvider(true, new ModTagsProvider.EntityTypes(output, registries));
        generator.addProvider(true, new ModTagsProvider.DamageType(output, registries, helper));
        generator.addProvider(true, new TestSpawnTableProvider(output, registries));
        generator.addProvider(true, new TestBonusProvider(output, registries, helper));
        generator.addProvider(true, new TestLanguageProvider(output));
        generator.addProvider(false, new ModLanguageProvider(output));
        generator.addProvider(false, new TestRecipeProvider(output, registries));
        generator.addProvider(true, new TestTextureProvider(helper, output));
        generator.addProvider(true, new TestGlobalLootModifierProvider(output, registries, "test"));
        generator.addProvider(true, new TestClientParticlePresetProvider(output));
        generator.addProvider(true, new TestServerParticlePresetProvider(output));
    }
}
