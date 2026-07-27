package net.kapitencraft.kap_lib.core.client.widget.text;

import net.minecraft.util.FormattedCharSequence;

/**
 * colorize the text inside a multiple line textbox
 */
@FunctionalInterface
public interface IFormatter {

    /**
     * @param text the text inside this line
     * @param lineIndex the index of this line
     * @return the colorized text
     */
    FormattedCharSequence format(String text, int lineIndex);

}
