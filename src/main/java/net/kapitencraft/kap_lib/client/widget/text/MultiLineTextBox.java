package net.kapitencraft.kap_lib.client.widget.text;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.widget.ScrollableWidget;
import net.kapitencraft.kap_lib.client.widget.background.WidgetBackground;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import net.kapitencraft.kap_lib.util.Vec2i;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class MultiLineTextBox extends ScrollableWidget {
    private static final int CURSOR_INSERT_COLOR = -3092272;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    public static final int DEFAULT_TEXT_COLOR = 14737632;
    private static final int BACKGROUND_COLOR = -16777216;
    private final Font font;
    /** Has the current text being edited on the textbox. */
    private final List<String> lineValues = new ArrayList<>();
    private final List<Integer> lineEndIndexes = new ArrayList<>();
    private String value = "";
    private int frame;
    /** if true the textbox can lose focus by clicking elsewhere on the screen */
    private boolean canLoseFocus = true;
    /** If this value is true along with isFocused, keyTyped will process the keys. */
    private boolean isEditable = true;
    private boolean shiftPressed;
    /** The current character index that should be used as start of the rendered text. */
    private Vec2i cursorPos2d = new Vec2i(0);
    private Vec2i highlightPos2d = new Vec2i(0);
    private int cursorPos;
    /** other selection position, maybe the same as the cursor */
    private int highlightPos;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textColorUnEditable = 7368816;
    private WidgetBackground background = WidgetBackground.fill(BACKGROUND_COLOR);
    @Nullable
    private ISuggestionProvider suggestionBuilder;
    private List<Suggestion> suggestions;
    private int suggestionOffset;
    private String suggestionKey;
    private int suggestionSelectIndex;
    private int suggestionsWidth;
    /**
     * whether, and how lines should be marked
    */
    private LineRenderType lineRenderType = LineRenderType.DISABLED;
    @Nullable
    private Consumer<String> responder;
    private Consumer<List<String>> textConsumer = (list)-> {};
    /** Called to check if the text is valid */
    private Predicate<String> filter = Objects::nonNull;
    private Consumer<Integer> lineCreationConsumer = i -> {};
    private Consumer<Integer> lineRemovedConsumer = i -> {};
    private BiConsumer<Integer, String> lineModificationConsumer = (integer, string) -> {};
    /**
     * formatter formatting line text with color;
     * <br> {@code string}: text content
     * <br> {@code i}: line index
     */
    private IFormatter formatter = (string, i) -> FormattedCharSequence.forward(string, Style.EMPTY);
    @Nullable
    private Component hint;

    public MultiLineTextBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        this(pFont, pX, pY, pWidth, pHeight, null, pMessage);
    }

    public MultiLineTextBox(Font pFont, int pX, int pY, int pWidth, int pHeight, @Nullable MultiLineTextBox pEditBox, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.font = pFont;
        if (pEditBox != null) {
            this.value = pEditBox.value;
            this.cursorPos2d = pEditBox.cursorPos2d;
            this.highlightPos2d = pEditBox.highlightPos2d;
            this.highlightPos = pEditBox.highlightPos;
            this.cursorPos = pEditBox.cursorPos;
            this.scrollX = pEditBox.scrollX;
            this.scrollY = pEditBox.scrollY;
        }
        this.applyText2d();
        this.scrollX = 2; this.scrollY = 2;
    }

    /**
     * set the color formatter for each line
     * <br> <i>this is not line sensitive, it only contains the String for each line</i>
     */
    public void setFormatter(IFormatter pTextFormatter) {
        this.formatter = pTextFormatter;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextColorUnEditable(int textColorUnEditable) {
        this.textColorUnEditable = textColorUnEditable;
    }

    public void setTextCallback(ITextCallback callback) {
        this.onLineCreated(callback::lineCreated);
        this.onLineModified(callback::lineModified);
        this.onLineRemoved(callback::lineRemoved);
    }

    public void setIDE(IDE ide) {
        this.setTextCallback(ide);
        this.setFormatter(ide);
        this.setSuggestionBuilder(ide);
    }

    /**
     * set the line creation hook
     * <br>called whenever a new line is added to the textbox
     */
    public void onLineCreated(Consumer<Integer> lineCreationConsumer) {
        this.lineCreationConsumer = lineCreationConsumer;
    }

    /**
     * set the line modification hook
     * <br>called whenever a line is modified
     */
    public void onLineModified(BiConsumer<Integer, String> lineModificationConsumer) {
        this.lineModificationConsumer = lineModificationConsumer;
    }

    public void onTextModified(Consumer<List<String>> textConsumer) {
        this.textConsumer = textConsumer;
    }

    /**
     * set the line remove hook
     * <br>called whenever a line is removed
     */
    public void onLineRemoved(Consumer<Integer> lineRemovedConsumer) {
        this.lineRemovedConsumer = lineRemovedConsumer;
    }

    /**
     * Increments the cursor counter
     */
    public void tick() {
        ++this.frame;
    }

    protected @NotNull MutableComponent createNarrationMessage() {
        Component component = this.getMessage();
        return Component.translatable("gui.narrate.editBox", component, this.value);
    }

    /**
     * Sets the text of the textbox, and moves the cursor to the end.
     */
    public void setValue(String pText) {
        if (this.filter.test(pText)) {
            this.value = pText;
            applyText2d();
            this.moveCursorToEnd();
            this.moveHighlightToCursor();
            this.onValueChange(pText);
        }
    }

    /**
     * Returns the 1d contents of the textbox
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Returns the text between the cursor and selectionEnd.
     */
    public String getHighlighted() {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(i, j);
    }

    /**
     * Adds the given text after the cursor, or replaces the currently selected text if there is a selection.
     */
    public void insertText(String pTextToWrite) {
        int selectionStart = Math.min(this.cursorPos, this.highlightPos);
        int selectionEnd = Math.max(this.cursorPos, this.highlightPos);
        int space = Integer.MAX_VALUE - this.value.length() - (selectionStart - selectionEnd);
        String insert = SharedConstants.filterText(pTextToWrite, true);
        int insertLength = insert.length();
        if (space < insertLength) {
            insert = insert.substring(0, space);
            insertLength = space;
        }

        updateText(insert, selectionStart, selectionEnd, insertLength);
    }

    private void updateText(String insert, int selectionStart, int selectionEnd, int insertLength) {
        String s1 = (new StringBuilder(this.value)).replace(selectionStart, selectionEnd, insert).toString();
        if (this.filter.test(s1)) {
            this.value = s1;
            this.setHighlightPos(this.cursorPos);
            updateText2d(insert, selectionStart, selectionEnd);
            this.onValueChange(this.value);
            this.setCursorPosition1d(selectionStart + Math.max(0, insertLength));
        }
    }

    private void updateText2d(String insert, int selectionStart, int selectionEnd) {
        List<String> insert2d = List.of(insert.split("\n", Integer.MAX_VALUE));
        Vec2i startLineIndex = get2dPositionFrom1dPosition(selectionStart);
        Vec2i endLineIndex = get2dPositionFrom1dPosition(selectionEnd);

        if (insert2d.isEmpty()) {
            KapLibMod.LOGGER.warn("trying to insert nothing into Multiline textbox; skipping!");
            return;
        }

        String lastLineRemaining = this.getFromEndSection(endLineIndex);
        if (insert2d.size() > 1) this.updateLine(startLineIndex.y, this.getFromStartSection(startLineIndex) + insert2d.get(0));
        int lineIndex = startLineIndex.y;
        for (int i = 1; i < insert2d.size(); i++) {
            if (lineIndex < endLineIndex.y) {
                this.updateLine(lineIndex, insert2d.get(i));
            } else {
                this.addLine(lineIndex + 1, insert2d.get(i));
            }
            lineIndex++;
        }
        int removeLineFirstIndex = Math.max(lineIndex, startLineIndex.y + 1);
        for (int i = removeLineFirstIndex; i <= endLineIndex.y; i++) {
            this.removeLine(removeLineFirstIndex);
        }
        this.updateLine(lineIndex, (insert2d.size() == 1 ? this.getFromStartSection(startLineIndex) : "") + insert2d.get(insert2d.size()-1) + lastLineRemaining);
    }

    private void notifyCreationAndChange(int index, boolean updateText) {
        this.lineEndIndexes.add(index, lineValues.get(index).length());
        this.lineCreationConsumer.accept(index);
        this.notifyChange(index, updateText);
    }

    private String getFromStartSection(Vec2i def) {
        return lineValues.get(def.y).substring(0, def.x);
    }

    private String getFromEndSection(Vec2i def) {
        return lineValues.get(def.y).substring(def.x);
    }

    private void updateLine(int index, String insert) {
        if (insert != null && !insert.equals(this.lineValues.get(index))) {
            this.lineValues.set(index, insert);
            this.notifyChange(index, true);
        }
    }

    private void addLine(int index, String insert) {
        this.lineValues.add(index, insert);
        this.notifyCreationAndChange(index, true);
    }

    private void removeLine(int index) {
        this.lineValues.remove(index);
        this.lineRemovedConsumer.accept(index);
    }

    private void notifyChange(int index, boolean updateText) {
        String value = this.lineValues.get(index);
        this.lineEndIndexes.set(index, value.length());
        this.lineModificationConsumer.accept(index, value);
        if (updateText) this.textConsumer.accept(this.lineValues);
    }

    private Vec2i get2dPositionFrom1dPosition(int pos) {
        int loc = 0;
        for (int i = 0; i < lineEndIndexes.size(); i++) {
            if (pos <= loc + lineEndIndexes.get(i)) {
                if (pos - loc < 0 || pos - loc > lineEndIndexes.get(i)) {
                    KapLibMod.LOGGER.error("illegal x location detected: {}", pos - loc);
                }
                return new Vec2i(pos - loc, i);
            }
            loc += lineEndIndexes.get(i);
            loc++;
        }
        return Vec2i.ZERO;
    }

    /**
     * reapply the simple value to its 2d list
     */
    private void applyText2d() {
        List<String> oldValues = new ArrayList<>(this.lineValues);
        this.lineValues.clear();
        StringBuilder builder = new StringBuilder();
        int lineIndex = 0;
        boolean textChanged = false;
        for (int i = 0; i < this.value.length(); i++) {
            char c = this.value.charAt(i);
            if (c == '\n') {
                lineValues.add(builder.toString());
                if (!Objects.equals(oldValues.get(lineIndex), lineValues.get(lineIndex))) {
                    this.notifyChange(lineIndex, false);
                    textChanged = true;
                }
                lineIndex++;
                builder = new StringBuilder();
            }
            else builder.append(c);
        }
        lineValues.add(builder.toString());
        if (lineValues.size() > oldValues.size()) {
            for (int i = oldValues.size(); i < lineValues.size(); i++) {
                this.notifyCreationAndChange(i, false);
                textChanged = true;
            }
        }
        if (textChanged) textConsumer.accept(this.lineValues);
    }

    private void onValueChange(String pNewText) {
        if (this.responder != null) {
            this.responder.accept(pNewText);
        }

    }

    private void deleteText(int pCount) {
        if (Screen.hasControlDown()) {
            this.deleteWords(pCount);
        } else {
            this.deleteChars(pCount);
        }

    }

    /**
     * Deletes the given number of words from the current cursor's position, unless there is currently a selection, in
     * which case the selection is deleted instead.
     */
    public void deleteWords(int pNum) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteChars(this.getWordPosition(pNum) - this.cursorPos);
            }
        }
    }

    /**
     * Deletes the given number of characters from the current cursor's position, unless there is currently a selection,
     * in which case the selection is deleted instead.
     */
    public void deleteChars(int pNum) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int i = this.getCursorPos(pNum);
                int j = Math.min(i, this.cursorPos);
                int k = Math.max(i, this.cursorPos);
                if (j != k) {
                    updateText("", j, k, pNum);
                }
            }
        }
    }

    /**
     * Gets the starting index of the word at the specified number of words away from the cursor position.
     */
    public int getWordPosition(int pNumWords) {
        return this.getWordPosition(pNumWords, this.getCursorPosition());
    }

    /**
     * Gets the starting index of the word at a distance of the specified number of words away from the given position.
     */
    private int getWordPosition(int pN, int pPos) {
        return this.getWordPosition(pN, pPos, true);
    }

    /**
     * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
     */
    private int getWordPosition(int pN, int pPos, boolean pSkipWs) {
        int i = pPos;
        boolean flag = pN < 0;
        int j = Math.abs(pN);

        for(int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.value.length();
                i = this.value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while(pSkipWs && i < l && this.value.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while(pSkipWs && i > 0 && this.value.charAt(i - 1) == ' ') {
                    --i;
                }

                while(i > 0 && this.value.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    private int getWordPosition2d(int pN) {
        return getWordPosition2d(pN, this.cursorPos2d);
    }

    private int getWordPosition2d(int pN, Vec2i pPos) {
        return getWordPosition2d(pN, pPos, true);
    }

    private int getWordPosition2d(int pN, Vec2i pPos, boolean pSkipWs) {
        int i = pPos.x;
        boolean flag = pN < 0;
        int j = Math.abs(pN);
        String value = this.lineValues.get(pPos.y);

        for(int k = 0; k < j; ++k) {
            if (!flag) {
                int l = value.length();
                i = value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while(pSkipWs && i < l && value.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while(pSkipWs && i > 0 && value.charAt(i - 1) == ' ') {
                    --i;
                }

                while(i > 0 && value.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    /**
     * Moves the text cursor by a specified number of characters
     */
    public void moveCursor(int pDelta) {
        this.setCursorPosition2d(this.getCursorPos(pDelta));
    }

    /**
     * Moves the text cursor by a specified number of lines
     */
    public void moveCursorVertical(int pDelta) {
        int curXWidth = this.font.width(this.lineValues.get(this.cursorPos2d.y).substring(0, this.cursorPos2d.x));
        int newY = Mth.clamp(this.cursorPos2d.y + pDelta, 0,  this.lineValues.size() - 1);
        String subs = this.font.plainSubstrByWidth(this.lineValues.get(newY), curXWidth);
        this.setCursorPosition2d(subs.length(), newY);
    }

    private int getCursorPos(int pDelta) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, pDelta);
    }

    /**
     * Sets the current position of the cursor.
     */
    public void setCursorPosition2d(int pPos) {
        this.setCursorPosition1d(pPos);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    private void setCursorPosition2d(int xPos, int yPos) {
        this.cursorPos2d = new Vec2i(xPos, yPos);
        int pos = 0;
        int line = 0;
        for (String s : lineValues) {
            if (line == yPos) break;
            pos += s.length();
            pos++; //line break
            line++;
        }
        this.cursorPos = pos + xPos;
        reapplyPositionsAndScroll();
    }

    /**
     * Sets the position of the text cursor
     */
    public void setCursorPosition1d(int pPos) {
        this.cursorPos = Mth.clamp(pPos, 0, this.value.length());
        reapplyPositionsAndScroll();
    }

    private void reapplyCursor2d() {
        int loc = 0;
        for (int i = 0; i < lineEndIndexes.size(); i++) {
            if (cursorPos <= loc + lineEndIndexes.get(i)) {
                this.cursorPos2d = new Vec2i(cursorPos - loc, i);
                if (cursorPos - loc < 0 || cursorPos - loc > lineEndIndexes.get(i)) {
                    KapLibMod.LOGGER.error("cursor index set to illegal x state: {}", cursorPos - loc);
                }
                break;
            }
            loc += lineEndIndexes.get(i);
            loc++;
        }
        reapplySuggestions();
    }

    private void applySuggestions() {
        List<String> suggestions;
        if (suggestionBuilder == null) suggestions = List.of(
                this.suggestionKey,
                "aaaaaa", //test
                "abcdefgh" //test
        );
        else {
            suggestions = this.suggestionBuilder.suggestions(this.suggestionKey);
        }
        List<Suggestion> suggestionList = new ArrayList<>();
        suggestions.forEach(string1 -> {
            if (string1.isEmpty()) return; //make sure to skip empty suggestions
            int markIndex = TextHelper.getMatchingAmount(this.suggestionKey, string1);
            if (markIndex == 0) return; //make sure to skip non-matching suggestions

            suggestionList.add(new Suggestion(markIndex, string1));
        });
        this.suggestions = suggestionList;
        if (this.suggestionSelectIndex >= this.suggestions.size()) this.suggestionSelectIndex = 0;
        this.suggestionsWidth = this.suggestions.stream().mapToInt(sug -> sug.getWidth(font)).max().orElse(0);
    }

    private void enterSuggestion() {
        Suggestion suggestion = this.suggestions.get(suggestionSelectIndex);
        suggestion.insertString(this::insertText);
    }


    private void reapplySuggestions() {
        int suggestionOffsetIndex = Math.max(0, this.getWordPosition2d(-1));
        this.suggestionKey = this.lineValues.get(this.cursorPos2d.y)
                .substring(suggestionOffsetIndex, this.cursorPos2d.x);
        this.suggestionOffset = this.font.width(this.getFromStartSection(new Vec2i(suggestionOffsetIndex, this.cursorPos2d.y)));
        this.applySuggestions();
    }

    private boolean hasSuggestions() {
        return this.suggestions != null && !this.suggestions.isEmpty();
    }

    private void reapplyHighlight2d() {
        int loc = 0;
        for (int i = 0; i < lineValues.size(); i++) {
            if (highlightPos <= loc + lineEndIndexes.get(i)) {
                this.highlightPos2d = new Vec2i(highlightPos - loc, i);
                if (highlightPos - loc < 0 || highlightPos - loc > this.lineEndIndexes.get(i)) {
                    KapLibMod.LOGGER.error("highlight index set to illegal x state: {}", highlightPos - loc);
                }
                return;
            }
            loc += lineEndIndexes.get(i);
            loc++;
        }
    }

    private void reapplyPositionsAndScroll() {
        reapplyCursor2d();
        this.updateScroll(false);
        if (!Screen.hasShiftDown()) this.moveHighlightToCursor();
    }

    /**
     * Moves the cursor to the very start of this text box.
     */
    public void moveCursorToStart() {
        this.setCursorPosition2d(0);
    }

    /**
     * Moves the cursor to the very end of this text box.
     */
    public void moveCursorToEnd() {
        this.setCursorPosition2d(this.value.length());
    }

    /**
     * Called when a keyboard key is pressed within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     * @param pKeyCode the key code of the pressed key.
     * @param pScanCode the scan code of the pressed key.
     * @param pModifiers the keyboard modifiers.
     */
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (!this.canConsumeInput()) {
            return false;
        } else {
            this.shiftPressed = Screen.hasShiftDown();
            if (Screen.isSelectAll(pKeyCode)) {
                this.moveCursorToEnd();
                this.setHighlightPos(0);
                return true;
            } else if (Screen.isCopy(pKeyCode)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                return true;
            } else if (Screen.isPaste(pKeyCode)) {
                if (this.isEditable) {
                    this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                }

                return true;
            } else if (Screen.isCut(pKeyCode)) {
                Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                if (this.isEditable) {
                    this.insertText("");
                }

                return true;
            } else {
                return switch (pKeyCode) {

                    case 256 -> {
                        this.setFocused(false);
                        yield true;
                    }
                    case 257 -> {
                        if (this.hasSuggestions()) this.enterSuggestion();
                        else this.insertText("\n");
                        yield true;
                    }
                    case 258 -> {
                        if (this.hasSuggestions()) this.enterSuggestion();
                        else this.insertText("\t");
                        yield true;
                    }
                    case 259 -> {
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(-1);
                            this.shiftPressed = Screen.hasShiftDown();
                        }
                        yield true;
                    }
                    default -> false;
                    case 261 -> {
                        //delete one
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(1);
                            this.shiftPressed = Screen.hasShiftDown();
                        }
                        yield true;
                    }
                    case 262 -> {
                        //move right
                        if (Screen.hasControlDown()) {
                            this.setCursorPosition2d(this.getWordPosition(1));
                        } else {
                            this.moveCursor(1);
                        }
                        yield true;
                    }
                    case 263 -> {
                        //move left
                        if (Screen.hasControlDown()) {
                            this.setCursorPosition2d(this.getWordPosition(-1));
                        } else {
                            this.moveCursor(-1);
                        }
                        yield true;
                    }
                    case 264 -> {
                        //move down
                        if (hasSuggestions()) {
                            if (this.suggestionSelectIndex  == this.suggestions.size() - 1) {
                                this.suggestionSelectIndex = 0;
                            } else this.suggestionSelectIndex++;
                        } else moveCursorVertical(1);
                        yield true;
                    }
                    case 265 -> {
                        //move up
                        if (hasSuggestions()) {
                            if (this.suggestionSelectIndex  == 0) {
                                this.suggestionSelectIndex = this.suggestions.size() - 1;
                            } else this.suggestionSelectIndex--;
                        } else moveCursorVertical(-1);
                        yield true;
                    }
                    case 268 -> {
                        this.moveCursorToStart();
                        yield true;
                    }
                    case 269 -> {
                        this.moveCursorToEnd();
                        yield true;
                    }
                };
            }
        }
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    /**
     * Called when a character is typed within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     * @param pCodePoint the code point of the typed character.
     * @param pModifiers the keyboard modifiers.
     */
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (SharedConstants.isAllowedChatCharacter(pCodePoint)) {
            if (this.isEditable) {
                this.insertText(Character.toString(pCodePoint));
            }


            return true;
        } else {
            return false;
        }
    }

    /**
     * Called when a mouse click occurs
     */
    public void onClick(double pMouseX, double pMouseY) {
        if (this.value.isEmpty()) {
            this.setCursorPosition2d(0);
            return;
        }
        int xOffset = Mth.floor(pMouseX) - this.getX() - Mth.floor(this.scrollX) - this.getLineMarkerWidth();
        int yOffset = Mth.floor(pMouseY) - this.getY() - Mth.floor(this.scrollY);
        int line = Mth.clamp(yOffset / 10, 0, this.lineValues.size() - 1);

        this.setCursorPosition2d(this.font.plainSubstrByWidth(this.lineValues.get(line), xOffset).length(), line);
    }

    private void moveHighlightToCursor() {
        this.highlightPos = this.cursorPos;
        this.highlightPos2d = this.cursorPos2d;
    }

    /**
     * call when closing the widget
     */
    public void onClose() {
        ClientHelper.changeCursorType(GLFW.GLFW_ARROW_CURSOR);
    }

    public void playDownSound(@NotNull SoundManager pHandler) {
    }

    public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        //modify cursor
        ClientHelper.changeCursorType(this.isHovered() ? GLFW.GLFW_IBEAM_CURSOR : GLFW.GLFW_ARROW_CURSOR);

        this.background.render(false, pGuiGraphics, getX(), getY(), this.width, this.height, this.scrollX, this.scrollY);

        int textXStart = this.getX() + this.getLineMarkerWidth();

        pGuiGraphics.enableScissor(textXStart, getY(), getX() + this.width, getY() + this.height);

        //actual text
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float)textXStart, (float)getY(), 0.0F);
        int textColor = this.isEditable ? this.textColor : this.textColorUnEditable;
        int x = Mth.floor(this.scrollX);
        int yBase = Mth.floor(this.scrollY);
        int cursorX = x + this.font.width(getFromStartSection(cursorPos2d));
        for (int lineIndex = 0; lineIndex < lineValues.size(); lineIndex++) {

            String line = lineValues.get(lineIndex);
            boolean cursorInLine = cursorPos2d.y == lineIndex;
            boolean showCursor = this.isFocused() && this.frame / 6 % 2 == 0 && cursorInLine;
            int y = yBase + lineIndex * 10;

            if (!line.isEmpty()) {
                pGuiGraphics.drawString(this.font, this.formatter.format(line, lineIndex), x, y, textColor);
            }

            if (showCursor) {
                if (!this.value.isEmpty()) {
                    pGuiGraphics.fill(RenderType.guiOverlay(), cursorX, y - 1, cursorX + 1, y + 1 + 9, CURSOR_INSERT_COLOR);
                } else {
                    pGuiGraphics.drawString(this.font, CURSOR_APPEND_CHARACTER, cursorX, y, textColor);
                }
            }

            if (highlightPos != cursorPos) {
                renderHighlight(lineIndex, line, pGuiGraphics, x, y);
            }
        }

        pGuiGraphics.pose().popPose();
        pGuiGraphics.disableScissor();

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(this.getX(), this.getY(), 0);
        if (this.lineRenderType != LineRenderType.DISABLED) {
            pGuiGraphics.enableScissor(getX(), getY(), textXStart, getY() + this.height);
            for (int i = 0; i < lineValues.size(); i++) {
                if (i % this.lineRenderType.lineOffset == 0) pGuiGraphics.drawString(this.font, String.valueOf(i+1), 1, yBase + i * 10, textColor);
            }
            pGuiGraphics.disableScissor();
        }
        this.renderSuggestions(pGuiGraphics, this.suggestionOffset + this.getLineMarkerWidth() + x, yBase + (cursorPos2d.y + 1) * 10);
        pGuiGraphics.pose().popPose();
    }

    private void renderSuggestions(GuiGraphics graphics, int renderStart, int y) {
        graphics.pose().translate(0, 0, 200);
        if (this.suggestions != null && !this.suggestions.isEmpty()) {
            graphics.fill(renderStart - 1, y, renderStart + suggestionsWidth, y + 2 + 10*suggestions.size(), 0xFF505050);
            for (int i = 0; i < suggestions.size(); i++) {
                if (suggestionSelectIndex == i) graphics.fill(renderStart - 1, y + i * 10, renderStart + suggestionsWidth, y + 12 + i * 10, 0xFFC4CfC4);
                graphics.drawString(this.font, suggestions.get(i).getRenderable(), renderStart, y + 1 + 10*i, -1);
            }
        }
    }

    private int getLineMarkerWidth() {
        return this.lineRenderType == LineRenderType.DISABLED ? 0 : 40;
    }

    private void renderHighlight(int lineIndex, String line, GuiGraphics graphics, int x, int y) {
        Vec2i highlightStart = getHighlightPos(true);
        Vec2i highlightEnd = getHighlightPos(false);
        String highlighted = line;
        int highlightStartIndex = 0, highlightEndIndex = line.length();
        if (highlightEnd.y == lineIndex) {
            highlighted = highlighted.substring(0, highlightEnd.x);
            highlightEndIndex = highlightEnd.x;
        }
        if (highlightStart.y == lineIndex) {
            highlighted = highlighted.substring(highlightStart.x);
            highlightStartIndex = highlightStart.x;
        }
        if (highlightStart.y > lineIndex || highlightEnd.y < lineIndex) {
            highlighted = "";
        }
        if (!highlighted.isEmpty()) {
            int highlightOffset = this.font.width(line.substring(0, highlightStartIndex));
            int highlightWidth = this.font.width(line.substring(highlightStartIndex, highlightEndIndex));
            this.renderHighlight(graphics, x + highlightOffset, y - 1, x + highlightOffset + highlightWidth, y + 10);
        }
    }

    private Vec2i getHighlightPos(boolean start) {
        return highlightPos < cursorPos == start ? highlightPos2d : cursorPos2d;
    }

    private void renderHighlight(GuiGraphics pGuiGraphics, int pMinX, int pMinY, int pMaxX, int pMaxY) {
        if (pMinX < pMaxX) {
            int i = pMinX;
            pMinX = pMaxX;
            pMaxX = i;
        }

        if (pMinY < pMaxY) {
            int j = pMinY;
            pMinY = pMaxY;
            pMaxY = j;
        }

        if (pMaxX > this.getX() + this.width) {
            pMaxX = this.getX() + this.width;
        }

        if (pMinX > this.getX() + this.width) {
            pMinX = this.getX() + this.width;
        }

        pGuiGraphics.fill(RenderType.guiTextHighlight(), pMinX, pMinY, pMaxX, pMaxY, -16776961);
    }
    /**
     * Returns the current position of the cursor.
     */
    public int getCursorPosition() {
        return this.cursorPos;
    }

    /**
     * Retrieves the next focus path based on the given focus navigation event.
     * <p>
     * @return the next focus path as a ComponentPath, or {@code null} if there is no next focus path.
     * @param pEvent the focus navigation event.
     */
    @Nullable
    public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent pEvent) {
        return this.visible && this.isEditable ? super.nextFocusPath(pEvent) : null;
    }

    /**
     * Checks if the given mouse coordinates are over the GUI element.
     * <p>
     * @return {@code true} if the mouse is over the GUI element, {@code false} otherwise.
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     */
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return this.visible && MathHelper.is2dBetween(pMouseX, pMouseY, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
    }

    /**
     * Sets the focus state of the GUI element.
     * @param pFocused {@code true} to apply focus, {@code false} to remove focus
     */
    public void setFocused(boolean pFocused) {
        if (this.canLoseFocus || pFocused) {
            super.setFocused(pFocused);
            if (pFocused) {
                this.frame = 0;
            }
        }
    }

    private boolean isEditable() {
        return this.isEditable;
    }

    /**
     * Sets whether this text box is enabled. Disabled text boxes cannot be typed in.
     */
    public void setEditable(boolean pEnabled) {
        this.isEditable = pEnabled;
    }

    /**
     * Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the
     * selection). If the anchor is set beyond the bounds of the current text, it will be put back inside.
     */
    public void setHighlightPos(int pPosition) {
        int i = this.value.length();
        this.highlightPos = Mth.clamp(pPosition, 0, i);
        this.reapplyHighlight2d();
    }

    public void setLineRenderType(LineRenderType type) {
        this.lineRenderType = type;
    }

    /**
     * Returns {@code true} if this textbox is visible.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets whether this textbox is visible.
     */
    public void setVisible(boolean pIsVisible) {
        this.visible = pIsVisible;
    }

    public void setSuggestionBuilder(@Nullable ISuggestionProvider pSuggestion) {
        this.suggestionBuilder = pSuggestion;
    }

    public void setTextChangeHook(Consumer<List<String>> textConsumer) {
        this.textConsumer = textConsumer;
    }

    public int getScreenX(int pCharNum) {
        return pCharNum > this.value.length() ? this.getX() : this.getX() + this.font.width(this.value.substring(0, pCharNum));
    }

    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }

    /**
     * sets the background of this text box to a {@link ResourceLocation}
     * @param backgroundLocation the texture location
     */
    public void setTextureBackground(ResourceLocation backgroundLocation) {
        this.background = WidgetBackground.texture(backgroundLocation, 16, 16);
    }

    /**
     * sets the background of this Widget to the parameter
     */
    public void setBackground(WidgetBackground background) {
        this.background = background;
    }

    @Override
    protected void updateScroll(boolean ignoreCursor) {
        if (canScroll(false)) {
            if (!ignoreCursor) {
                if ((this.cursorPos2d.y + 1) * 10 + this.scrollY >= this.height) {
                    this.scrollY = this.height - (this.cursorPos2d.y + 1) * 10;
                } else if (this.cursorPos2d.y * 10 + this.scrollY < ClientModConfig.getCursorMoveOffset() * 10) {
                    this.scrollY += 10;
                }
            }
            this.scrollY = Mth.clamp(this.scrollY, -this.valueSize(false) + this.height + 2, 2);
        } else this.scrollY = 2;
        if (canScroll(true)) {
            if (!ignoreCursor) {
                int offsetWidth = this.font.width(this.lineValues.get(this.cursorPos2d.y).substring(0, this.cursorPos2d.x));
                if (offsetWidth + this.scrollX >= this.width) {
                    this.scrollX = -offsetWidth + this.width - 2;
                }
            }
            this.scrollX = Mth.clamp(this.scrollX, -this.valueSize(true) + this.width + 2, 2);
        } else this.scrollX = 2;
    }

    @Override
    protected int valueSize(boolean x) {
        return x ? MathHelper.getLargest(this.lineValues.stream().map(this.font::width).toList()) : this.lineValues.size() * 10;
    }

    /**
     * line render type
     * how line markers should be rendered
     */
    public enum LineRenderType {
        DISABLED(0),
        EVERY(1),
        EVERY5(5),
        EVERY10(10);

        private final int lineOffset;

        LineRenderType(int lineOffset) {
            this.lineOffset = lineOffset;
        }
    }
}