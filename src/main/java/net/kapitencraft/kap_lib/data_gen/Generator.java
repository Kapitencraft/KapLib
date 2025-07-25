package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.data_gen.tags.ModTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Generator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        lookupProvider = generator.addProvider(true, new ModRegistriesProvider(output, lookupProvider)).getRegistryProvider();
        generator.addProvider(true, new ExtraNumbersLangProvider(output));
        CompletableFuture<TagsProvider.TagLookup<Block>> blockTagLookup = generator.addProvider(true, new ModTagsProvider.Block(output, lookupProvider, helper)).contentsGetter();
        generator.addProvider(true, new ModTagsProvider.EntityTypes(output, lookupProvider));
        generator.addProvider(true, new ModTagsProvider.DamageType(output, lookupProvider, helper));
        generator.addProvider(true, new TestSpawnTableProvider(output));
        generator.addProvider(true, new TestBonusProvider(output, lookupProvider, helper));
        generator.addProvider(true, new TestLanguageProvider(output));
        generator.addProvider(false, new ModLanguageProvider(output));
        generator.addProvider(false, new TestRecipeProvider(output));
    }
}
