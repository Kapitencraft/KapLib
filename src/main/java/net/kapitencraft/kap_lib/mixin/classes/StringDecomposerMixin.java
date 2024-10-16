package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.ModGlyphEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StringDecomposer.class)
public class StringDecomposerMixin {

    /**
     * @author Kapitencraft
     * @reason custom display effect application
     */
    @Overwrite
    public static boolean iterateFormatted(String s, int length, Style style, Style style2, FormattedCharSink sink) {
        int i = s.length();
        Style formattedStyle = style;

        TextColor nonPingColor = null;
        boolean pinged = false;
        for(int j = length; j < i; ++j) {

            char c0 = s.charAt(j);
            if (c0 == 167) { //167 = §
                j++;
                if (j >= i) {
                    break;
                }

                char c1 = s.charAt(j);
                ChatFormatting format = ChatFormatting.getByCode(c1);
                if (format != null) {
                    formattedStyle = format == ChatFormatting.RESET ? style2 : formattedStyle.applyLegacyFormat(format);
                } else if (ModGlyphEffects.effectsForKey().containsKey(c1)) {
                    GlyphEffect effect = ModGlyphEffects.effectsForKey().get(c1);
                    formattedStyle = MiscHelper.withSpecial(formattedStyle, effect);
                }
            } else if (Character.isHighSurrogate(c0)) {
                if (j + 1 >= i) {
                    if (!sink.accept(j, formattedStyle, 65533)) {
                        return false;
                    }
                    break;
                }

                char c2 = s.charAt(j + 1);
                if (Character.isLowSurrogate(c2)) {
                    if (!sink.accept(j, formattedStyle, Character.toCodePoint(c0, c2))) {
                        return false;
                    }

                    ++j;
                } else if (!sink.accept(j, formattedStyle, 65533)) {
                    return false;
                }
            } else if (!feedChar(formattedStyle, sink, j, c0)) {
                return false;
            }
        }

        return true;
    }

    @Shadow
    private static boolean feedChar(Style style, FormattedCharSink sink, int j, char c) {return false;}
}