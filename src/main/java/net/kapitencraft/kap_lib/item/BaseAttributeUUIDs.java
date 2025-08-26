package net.kapitencraft.kap_lib.item;

import net.kapitencraft.kap_lib.event.custom.GatherBaseAttributeUUIDsEvent;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseAttributeUUIDs {
    private static final Map<UUID, Attribute> ids = new HashMap<>();

    public static final UUID ENTITY_REACH = UUID.fromString("c36d52b7-31c5-4717-84d4-841e74545a97");
    public static final UUID MINING_SPEED = UUID.fromString("78363f90-c362-463d-a9f6-faa115a9b35d");
    public static final UUID CRIT_DAMAGE = UUID.fromString("89e1810a-9c30-4920-aaa9-93062c6496f3");
    public static final UUID CRIT_CHANCE = UUID.fromString("5916c4e9-40d9-414a-964b-05c576b22ecc");
    public static final UUID STRENGTH = UUID.fromString("50dac057-c48f-41a6-a8a6-d29e4d8e3e5a");
    public static final UUID LUCK = UUID.fromString("10bd4edd-0f43-4d4c-81d1-781f646a01e0");
    public static final UUID BONUS_ATTACK_SPEED = UUID.fromString("cce42e73-50e8-432f-9ba7-f5d73319dcc8");
    public static final UUID RANGED_DAMAGE = UUID.fromString("9f396171-85ad-4e41-ab96-3bec22a0fc4b");
    public static final UUID DRAW_SPEED = UUID.fromString("23a8511b-3454-455e-b426-337d505229da");
    public static final UUID PROJECTILE_SPEED = UUID.fromString("404a4d0d-1546-4bae-8515-efc3244028a9");

    public static void init() {
        register(ENTITY_REACH, ForgeMod.ENTITY_REACH.get());
        register(MINING_SPEED, ExtraAttributes.MINING_SPEED.get());
        register(CRIT_DAMAGE, ExtraAttributes.CRIT_DAMAGE.get());
        register(CRIT_CHANCE, ExtraAttributes.CRIT_CHANCE.get());
        register(STRENGTH, ExtraAttributes.STRENGTH.get());
        register(LUCK, Attributes.LUCK);
        register(BONUS_ATTACK_SPEED, ExtraAttributes.BONUS_ATTACK_SPEED.get());
        register(RANGED_DAMAGE, ExtraAttributes.RANGED_DAMAGE.get());
        register(DRAW_SPEED, ExtraAttributes.DRAW_SPEED.get());
        register(PROJECTILE_SPEED, ExtraAttributes.PROJECTILE_SPEED.get());
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

    public static @Nullable Attribute get(UUID id) {
        return ids.get(id);
    }
}
