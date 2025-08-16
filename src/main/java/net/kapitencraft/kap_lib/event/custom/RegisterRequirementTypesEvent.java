package net.kapitencraft.kap_lib.event.custom;

import net.kapitencraft.kap_lib.requirements.type.RequirementType;
import net.neoforged.bus.api.Event;

import java.util.function.Consumer;

/**
 * used to register custom Requirement types
 * <br>type's usages need to be registered by the developer
 */
public class RegisterRequirementTypesEvent extends Event {
    private final Consumer<RequirementType<?>> consumer;
    public RegisterRequirementTypesEvent(Consumer<RequirementType<?>> consumer) {
        this.consumer = consumer;
    }

    /**
     * adds this requirement type to the manager
     */
    public void add(RequirementType<?> type) {
        consumer.accept(type);
    }
}
