package net.kapitencraft.kap_lib.client.widget.text;

import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.List;

/**
 * create suggestions from a string
 * <br> mostly not used alone and instead used together with a {@link IFormatter} to create something like an IDE
 */
public interface ISuggestionProvider extends GuiEventListener {

    /**
     * @param in the text to create suggestions from
     * @return a list of suggestions
     */
    List<String> suggestions(String in);
}
