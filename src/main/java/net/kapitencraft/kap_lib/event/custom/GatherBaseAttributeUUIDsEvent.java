package net.kapitencraft.kap_lib.event.custom;

import net.kapitencraft.kap_lib.item.BaseAttributeUUIDs;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.UUID;

/**
 * used to register new Base Attributes.
 * use {@link BaseAttributeUUIDs#register(UUID, Attribute)} to register the UUIDs
 * fired on the mod bus
 */
public class GatherBaseAttributeUUIDsEvent extends Event implements IModBusEvent {
}
