package net.kapitencraft.kap_lib.shader.config;

import net.kapitencraft.kap_lib.core.client.menu.widget.drop_down.elements.Element;
import net.kapitencraft.kap_lib.shader.chroma.ChromaOrigin;
import net.kapitencraft.kap_lib.shader.chroma.ChromaType;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;

//TODO figure out how to do config compatible with KapLib and modules
public class ShaderClientModConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {

        BUILDER.pop().comment("data to determine how chroma text should be rendered").push("chroma");
        CHROMA_SPEED = BUILDER
                .comment("the speed of chroma")
                .defineInRange("speed", 4., 1., 50.);
        CHROMA_TYPE = BUILDER.comment("the type of chroma ")
                .defineEnum("type", ChromaType.LINEAR);
        CHROMA_SPACING = BUILDER.comment("how wide each color should be rendered\nwith large values = less spread")
                .defineInRange("spacing", 2., 0.5, 5.);
        CHROMA_ORIGIN = BUILDER.comment("where the origin of the chroma (e.g. it's rotation and animation direction) should be")
                .defineEnum("origin", ChromaOrigin.BOTTOM_RIGHT);
    }

    private static final ModConfigSpec.DoubleValue CHROMA_SPEED;
    private static final ModConfigSpec.EnumValue<ChromaType> CHROMA_TYPE;
    private static final ModConfigSpec.DoubleValue CHROMA_SPACING;
    private static final ModConfigSpec.EnumValue<ChromaOrigin> CHROMA_ORIGIN;

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static ChromaType getChromaType() {
        return CHROMA_TYPE.get();
    }

    public static float getChromaSpeed() {
        return (float) (double) CHROMA_SPEED.get();
    }
    public static float getChromaSpacing() {
        return (float) (double) CHROMA_SPACING.get();
    }
    public static ChromaOrigin getChromaOrigin() {
        return CHROMA_ORIGIN.get();
    }
}