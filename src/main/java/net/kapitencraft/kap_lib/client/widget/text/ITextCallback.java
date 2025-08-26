package net.kapitencraft.kap_lib.client.widget.text;

public interface ITextCallback {


    /**
     * called whenever a new line is added in the connected MultiLineTextBox
     * @param index line index at creation
     */
    void lineCreated(int index);


    /**
     * called whenever a line is modified
     * @param index line index of modification
     * @param content content of the given line
     */
    void lineModified(int index, String content);


    /**
     * called whenever a line is removed
     * @param index line index of removal
     */
    void lineRemoved(int index);
}
