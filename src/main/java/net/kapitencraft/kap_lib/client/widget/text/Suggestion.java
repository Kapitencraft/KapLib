package net.kapitencraft.kap_lib.client.widget.text;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class Suggestion {
    private final int insertIndex;
    private final String suggestionString;
    private final Component renderSuggestion;

    public Suggestion(int insertIndex, String suggestionString) {
        this(insertIndex, suggestionString, Component.literal(
                suggestionString.substring(0, insertIndex)).withStyle(ChatFormatting.BLUE)
                        .append(suggestionString.substring(insertIndex))
        );
    }

    public Suggestion(int insertIndex, String suggestionString, Component renderSuggestion) {
        this.insertIndex = insertIndex;
        this.suggestionString = suggestionString;
        this.renderSuggestion = renderSuggestion;
    }

    public void insertString(Consumer<String> toApply) {
        String toInsert = this.suggestionString.substring(insertIndex);
        toApply.accept(toInsert);
    }

    public Component getRenderable() {
        return renderSuggestion;
    }

    public int getWidth(Font font) {
        return font.width(this.renderSuggestion);
    }
}
