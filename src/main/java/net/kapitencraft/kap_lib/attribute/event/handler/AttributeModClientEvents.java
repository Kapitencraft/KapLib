package net.kapitencraft.kap_lib.attribute.event.handler;

import net.kapitencraft.kap_lib.attribute.ExtendedItemProperties;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber
public class AttributeModClientEvents {

    @SubscribeEvent
    public static void registerItemProperties(FMLClientSetupEvent event) {
        ItemProperties.register(Items.BOW, ResourceLocation.withDefaultNamespace("pull"), ExtendedItemProperties.BOW_PULL);
    }
}
