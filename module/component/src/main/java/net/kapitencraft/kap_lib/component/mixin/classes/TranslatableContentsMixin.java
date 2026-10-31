package net.kapitencraft.kap_lib.component.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.component.ComponentModule;
import net.kapitencraft.kap_lib.component.ComponentRegistries;
import net.kapitencraft.kap_lib.component.ParamStorage;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

@Mixin(TranslatableContents.class)
public class TranslatableContentsMixin {

    @WrapOperation(
            method = "decomposeTemplate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/FormattedText;of(Ljava/lang/String;)Lnet/minecraft/network/chat/FormattedText;"
            )
    )
    private FormattedText decomposeGlyphPatterns(String text, Operation<FormattedText> original) {
        List<FormattedText> elements = new ArrayList<>();
        readFormat(text, Style.EMPTY, elements);

        return FormattedText.composite(elements);
    }

    @Unique
    private static void readFormat(String text, Style style, List<FormattedText> sink) {
        Matcher matcher = ComponentModule.EFFECT_MARKER_PATTERN.matcher(text);
        int i = 0;
        for (; matcher.find(i); i = matcher.end() + 1) {
            sink.add(FormattedText.of(text.substring(i, matcher.start()), style));
            String key = matcher.group("key");
            GlyphEffect<?> effect = ComponentRegistries.GLYPH_EFFECTS.get(ResourceLocation.parse(key));
            if (effect == null) {
                LibConstants.LOGGER.warn("unknown glyph effect key: {}", key);
                readFormat(matcher.group("text"), style, sink);
            } else {
                String args = matcher.group("args");
                Map<String, String> params = new HashMap<>();
                if (args != null) {
                    String[] split = args.split("[ =]");
                    for (int j = 1; j < split.length; j += 2) {
                        if (params.put(split[j], split[j + 1]) != null) {
                            LibConstants.LOGGER.warn("duplicate param in glyph effect: {}", key);
                        }
                    }
                }
                readFormat(matcher.group("text"), style.addEffect(effect.createInstance(new ParamStorage(params))), sink);
            }
        }
        sink.add(FormattedText.of(text.substring(i), style));
    }
}
