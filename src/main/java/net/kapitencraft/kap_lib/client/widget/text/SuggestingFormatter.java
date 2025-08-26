package net.kapitencraft.kap_lib.client.widget.text;

import java.util.List;

/**
 * a connection from {@link ISuggestionProvider} and {@link IFormatter}
 */
public abstract class SuggestingFormatter implements ISuggestionProvider, IFormatter {
    private boolean focused;

    @Override
    public List<String> suggestions(String in) {
        return List.of();
    }

    @Override
    public void setFocused(boolean pFocused) {
        this.focused = pFocused;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }
}