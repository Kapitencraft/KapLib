package net.kapitencraft.kap_lib.client.widget.text;

/**
 * wrapper for {@link IFormatter}, {@link ISuggestionProvider} and {@link ITextCallback}
 * used inside {@link MultiLineTextBox#setIDE(IDE)}
 */
public interface IDE extends IFormatter, ISuggestionProvider, ITextCallback {
}
