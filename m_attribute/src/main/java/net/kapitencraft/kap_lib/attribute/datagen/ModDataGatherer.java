package net.kapitencraft.kap_lib.attribute.datagen;

import net.kapitencraft.kap_lib.attribute.damage.AttributeDamageTypes;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

@EventBusSubscriber
public class ModDataGatherer {
    private static final RegistrySetBuilder REGISTRIES = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, AttributeDamageTypes::bootstrap);

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {

        event.createDatapackRegistryObjects(REGISTRIES, Set.of("kap_lib"));
    }

}
