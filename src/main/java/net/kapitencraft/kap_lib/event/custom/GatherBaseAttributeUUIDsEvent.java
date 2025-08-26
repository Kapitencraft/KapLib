package net.kapitencraft.kap_lib.event.custom;

import net.kapitencraft.kap_lib.item.BaseAttributeUUIDs;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * used to register new Base Attributes.
 * fired on the mod bus only on the client
 */
public class GatherBaseAttributeUUIDsEvent extends Event implements IModBusEvent {

    public void register(UUID uuid, Attribute attribute) {
        BaseAttributeUUIDs.register(uuid, attribute);
    }

    public void register(UUID uuid, Supplier<Attribute> supplier) {
        register(uuid, supplier.get());
    }
}
