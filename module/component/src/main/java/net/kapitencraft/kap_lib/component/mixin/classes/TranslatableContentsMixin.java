package net.kapitencraft.kap_lib.component.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.component.ComponentRegistries;
import net.kapitencraft.kap_lib.component.ParamStorage;
import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.TextHelper;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Style style = Style.EMPTY;
        int length = text.length();
        int idx = 0;
        int oIdx = 0;
        while (idx < length - 1) {
            char c0 = text.charAt(idx);
            if (c0 == '<') {
                int sI = idx;
                boolean isRemove = text.charAt(++idx) == '/';
                if (isRemove) idx++;
                int lI = idx;
                while (idx < length && ResourceLocation.isAllowedInResourceLocation(c0 = text.charAt(idx)))
                    idx++;
                String location = text.substring(lI, idx);
                if (c0 != ' ' && c0 != '>') {
                    LibConstants.LOGGER.warn("syntax error in glyph effect key: {}", location);
                } else {
                    GlyphEffect<?> effect = ComponentRegistries.GLYPH_EFFECTS.get(ResourceLocation.parse(location));
                    if (effect == null) {
                        LibConstants.LOGGER.warn("unknown glyph effect key: {}", location);
                    } else {
                        if (sI - oIdx > 0)
                            elements.add(FormattedText.of(text.substring(oIdx, sI), style));
                        if (isRemove) {
                            if (!style.hasEffect(effect))
                                LibConstants.LOGGER.warn("glyph effect {} not applied to style", location);
                            else {
                                style = style.removeEffect(effect);
                            }
                        } else {
                            Map<String, String> params = new HashMap<>();
                            while (idx < length - 1 && text.charAt(idx) != '>') {
                                int kO = ++idx;
                                while (idx < length - 1 && text.charAt(idx) != '=')
                                    idx++;
                                String key = text.substring(kO, idx);
                                int vO = ++idx;
                                while (idx < length - 1 && TextHelper.isDigitOrDot(text.charAt(idx)))
                                    idx++;
                                if (params.put(key, text.substring(vO, idx)) != null) {
                                    LibConstants.LOGGER.warn("duplicate param in glyph effect: {}", key);
                                }
                            }
                            style = style.addEffect(effect.createInstance(new ParamStorage(params)));
                        }
                        oIdx = idx + 1;
                    }
                }
            }
            idx++;
        }
        elements.add(original.call(text.substring(oIdx, length)));
        return FormattedText.composite(elements);
    }
}
