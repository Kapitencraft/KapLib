package net.kapitencraft.kap_lib.cooldown;

import net.kapitencraft.kap_lib.cooldown.registry.CooldownAttributes;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

public class CooldownAttributeAdder {

    public static void addAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, CooldownAttributes.COOLDOWN_REDUCTION);
    }
}
