package net.kapitencraft.kap_lib.item;

import net.kapitencraft.kap_lib.event.custom.GatherBaseAttributeUUIDsEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseAttributeUUIDs {
    private static final Map<UUID, Attribute> ids = new HashMap<>();

    public static void init() {
        ModLoader.get().postEvent(new GatherBaseAttributeUUIDsEvent());
    }

    public static void register(UUID uuid, Attribute attribute) {
        if (ids.containsKey(uuid)) {
            throw new IllegalArgumentException("duplicate base attribute id '" + uuid.toString() + "'");
        } else if (ids.containsValue(attribute) || attribute == Attributes.ATTACK_DAMAGE || attribute == Attributes.ATTACK_SPEED) {
            throw new IllegalArgumentException("duplicate base attribute '" + ForgeRegistries.ATTRIBUTES.getKey(attribute) + "'");
        }
        ids.put(uuid, attribute);
    }
}
