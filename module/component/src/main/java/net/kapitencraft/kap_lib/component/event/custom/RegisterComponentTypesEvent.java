package net.kapitencraft.kap_lib.component.event.custom;

import net.minecraft.network.chat.ComponentContents;
import net.minecraft.util.StringRepresentable;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.List;

public class RegisterComponentTypesEvent extends Event implements IModBusEvent {
    private final List<StringRepresentable> contents;

    public RegisterComponentTypesEvent(List<StringRepresentable> contents) {
        this.contents = contents;
    }

    public void register(ComponentContents.Type<?> type) {
        contents.add(type);
    }
}
