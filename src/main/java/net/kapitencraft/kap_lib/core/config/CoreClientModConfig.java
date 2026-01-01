package net.kapitencraft.kap_lib.core.config;

import net.kapitencraft.kap_lib.core.client.menu.widget.drop_down.elements.Element;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;

public class CoreClientModConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {

        BUILDER.comment("Settings for GUI elements").push("gui");
        SCROLL_SCALE = BUILDER
                .comment("the scale of how quick tooltips are scrolled with")
                .defineInRange("scroll_scale", 5., 1, 100);
        FOCUS_TYPE = BUILDER
                .comment("what focus type should be used for highlighting")
                .defineEnum("focus_type", Element.FocusTypes.OUTLINE);
        CURSOR_MOVE_OFFSET = BUILDER.comment("how many lines below the top the cursor will start scrolling up")
                .defineInRange("cursor_move_offset", 2, 0, 5);

        BUILDER.pop();
        PING_COLOR = BUILDER
                .comment("determines the color which indicates pings")
                .defineEnum("ping_color", ChatFormatting.YELLOW, Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).toArray(ChatFormatting[]::new));
    }

    private static final ModConfigSpec.DoubleValue SCROLL_SCALE;
    private static final ModConfigSpec.EnumValue<Element.FocusTypes> FOCUS_TYPE;
    private static final ModConfigSpec.IntValue CURSOR_MOVE_OFFSET;

    private static final ModConfigSpec.EnumValue<ChatFormatting> PING_COLOR;

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static ChatFormatting getPingColor() {
        return PING_COLOR.get();
    }

    public static double getScrollScale() {
        return SCROLL_SCALE.get();
    }

    public static int getCursorMoveOffset() {
        return CURSOR_MOVE_OFFSET.get();
    }

    public static Element.FocusTypes getFocusType() {
        return FOCUS_TYPE.get();
    }
}