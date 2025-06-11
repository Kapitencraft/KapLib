package net.kapitencraft.kap_lib.registry.custom.core;

import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ModRegistryCallbacks {

    public static class WearableSlotsAddCallback implements IForgeRegistry.AddCallback<WearableSlot> {

        @SuppressWarnings("unchecked")
        @Override
        public void onAdd(IForgeRegistryInternal<WearableSlot> owner, RegistryManager stage, int id, ResourceKey<WearableSlot> key, WearableSlot obj, @Nullable WearableSlot oldObj) {
            Map<WearableSlot, TagKey<Item>> tagKeys = owner.getSlaveMap(WearableSlot.TAG_KEY_SLAVE_MAP, Map.class);
            ResourceLocation location = key.location();
            tagKeys.put(obj, TagKey.create(Registries.ITEM, location.withPath(s -> "wearable." + s)));
        }
    }

    public static class WearableSlotsCreateCallback implements IForgeRegistry.CreateCallback<WearableSlot> {

        @Override
        public void onCreate(IForgeRegistryInternal<WearableSlot> owner, RegistryManager stage) {
            owner.setSlaveMap(WearableSlot.TAG_KEY_SLAVE_MAP, new HashMap<WearableSlot, TagKey<Item>>());
        }
    }
}
