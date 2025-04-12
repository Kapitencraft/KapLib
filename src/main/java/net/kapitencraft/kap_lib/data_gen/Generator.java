package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.data_gen.tags.ModTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
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
        generator.addProvider(true, new ExtraNumbersLangProvider(output));
        generator.addProvider(true, new ModRegistriesProvider(output, lookupProvider));
        generator.addProvider(true, new ModTagsProvider.Block(output, lookupProvider, helper));
        generator.addProvider(true, new ModTagsProvider.EntityTypes(output, lookupProvider));
        generator.addProvider(true, new ModTagsProvider.DamageType(output, lookupProvider, helper));
        generator.addProvider(true, new TestSpawnTableProvider(output));
    }
}
