package net.kapitencraft.kap_lib.config;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.chroma.ChromaOrigin;
import net.kapitencraft.kap_lib.client.chroma.ChromaType;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements.Element;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = KapLibMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();


    static {
        BUILDER.comment("settings for GUI elements").push("gui");
        SCROLL_SCALE = BUILDER
                .comment("the scale of how quick tooltips are scrolled with")
                .defineInRange("scroll_scale", 5., 1, 100);
        FOCUS_TYPE = BUILDER
                .comment("what focus type should be used for highlighting")
                .defineEnum("focus_type", Element.FocusTypes.OUTLINE);
        CURSOR_MOVE_OFFSET = BUILDER.comment("how many lines below the top the cursor will start scrolling up")
                        .defineInRange("cursor_move_offset", 2, 0, 5);

        BUILDER.pop().comment("data to determine how chroma text should be rendered").push("chroma");
        CHROMA_SPEED = BUILDER
                .comment("the speed of chroma")
                .defineInRange("speed", 4., 1., 50.);
        CHROMA_TYPE = BUILDER.comment("the type of chroma ")
                .defineEnum("type", ChromaType.LINEAR);
        CHROMA_SPACING = BUILDER.comment("how wide each color should be rendered\nwith large values = less spread")
                .defineInRange("spacing", 2., 0.5, 5.);
        CHROMA_ORIGIN = BUILDER.comment("where the origin of the chroma (eg it's rotation and animation direction) should be")
                .defineEnum("origin", ChromaOrigin.BOTTOM_RIGHT);

        BUILDER.pop();
        PING_COLOR = BUILDER
                .comment("determines the color which indicates pings")
                .defineEnum("ping_color", ChatFormatting.YELLOW, Arrays.stream(ChatFormatting.values()).filter(ChatFormatting::isColor).toArray(ChatFormatting[]::new));
    }

    private static final ForgeConfigSpec.DoubleValue SCROLL_SCALE;
    private static final ForgeConfigSpec.EnumValue<Element.FocusTypes> FOCUS_TYPE;
    private static final ForgeConfigSpec.DoubleValue CHROMA_SPEED;
    private static final ForgeConfigSpec.EnumValue<ChromaType> CHROMA_TYPE;
    private static final ForgeConfigSpec.DoubleValue CHROMA_SPACING;
    private static final ForgeConfigSpec.EnumValue<ChromaOrigin> CHROMA_ORIGIN;
    private static final ForgeConfigSpec.EnumValue<ChatFormatting> PING_COLOR;
    private static final ForgeConfigSpec.IntValue CURSOR_MOVE_OFFSET;

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static ChatFormatting getPingColor() {
        return PING_COLOR.get();
    }

    public static double getScrollScale() {
        return SCROLL_SCALE.get();
    }

    public static int getCursorMoveOffset() {
        return CURSOR_MOVE_OFFSET.get();
    }

    public static ChromaType getChromaType() {
        return CHROMA_TYPE.get();
    }

    public static float getChromaSpeed() {
        return (float) (double) CHROMA_SPEED.get();
    }

    public static float getChromaSpacing() {
        return (float) (double) CHROMA_SPACING.get();
    }

    public static Element.FocusTypes getFocusType() {
        return FOCUS_TYPE.get();
    }

    public static ChromaOrigin getChromaOrigin() {
        return CHROMA_ORIGIN.get();
    }
}