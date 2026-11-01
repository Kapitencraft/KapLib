package net.kapitencraft.kap_lib.enchantment.compat;

import net.kapitencraft.kap_lib.component.font.effect.GlyphEffects;
import net.kapitencraft.kap_lib.component.font.effect.effects.RainbowEffect;
import net.kapitencraft.kap_lib.core.client.widget.select.SelectChatColorWidget;
import net.kapitencraft.kap_lib.shader.ShaderRenderTypes;
import net.kapitencraft.kap_lib.shader.chroma.ChromaOrigin;
import net.kapitencraft.kap_lib.shader.chroma.ChromaType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;

import java.util.List;

public class ComponentCompat {

    public static void appendChromaType(List<SelectChatColorWidget.ColorType> types) {
        types.add(new ChromaColorType());
    }

    public static boolean checkRainbowActive(Style style) {
        return style.hasEffect(GlyphEffects.RAINBOW);
    }

    private static class ChromaColorType implements SelectChatColorWidget.ColorType {
        private static final Style CHROMA_STYLE = Style.EMPTY.addEffect(new RainbowEffect.RainbowEffectInstance(ChromaOrigin.BOTTOM_RIGHT, 2, 4, ChromaType.LINEAR));

        @Override
        public void render(GuiGraphics graphics, int x, int y, int width) {
            graphics.fill(ShaderRenderTypes.FILL_CHROMA, x, y, x + width, y + width, 0);
        }

        @Override
        public Style getStyle() {
            return CHROMA_STYLE;
        }
    }
}
