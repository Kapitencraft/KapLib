package net.kapitencraft.kap_lib.attribute.datagen;

import net.kapitencraft.kap_lib.attribute.damage.AttributeDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class ModDataGatherer {
    private static final RegistrySetBuilder REGISTRIES = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, AttributeDamageTypes::bootstrap);

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {

        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        event.createDatapackRegistryObjects(REGISTRIES, Set.of("kap_lib"));
        event.addProvider(new ModEntityTypeTagsProvider(output, registries, helper));
    }
}
