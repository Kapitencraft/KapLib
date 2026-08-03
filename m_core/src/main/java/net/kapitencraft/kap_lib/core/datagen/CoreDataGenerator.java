package net.kapitencraft.kap_lib.core.datagen;

import net.kapitencraft.kap_lib.core.CoreModule;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = CoreModule.MODULE_ID)
public class CoreDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        event.addProvider(new CoreEntityTypeTagsProvider(output, event.getLookupProvider(), event.getExistingFileHelper()));
    }
}
