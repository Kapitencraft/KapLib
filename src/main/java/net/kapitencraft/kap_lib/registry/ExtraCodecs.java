package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.client.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.mixin.duck.IKapLibDataSource;
import net.kapitencraft.kap_lib.mixin.duck.attribute.IKapLibAttributeModifier;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public interface ExtraCodecs {
    Codec<ComponentContents> COMPONENT_TYPES = ExtraRegistries.COMPONENT_CONTENT_TYPES.byNameCodec().dispatchStable(IKapLibComponentContents::codecFromVanilla, Function.identity());
    Codec<Component> COMPONENT = COMPONENT_TYPES.xmap(MutableComponent::create, Component::getContents);
    Codec<Object[]> TRANSLATABLE_COMPONENT_ARGS = COMPONENT.listOf().xmap(list -> {
                Object[] array = new Object[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    array[i] = Component.Serializer.unwrapTextArgument(list.get(i));
                }
                return array;
            },
            objects -> {
                List<Component> components = new ArrayList<>();
                for (Object o : objects) {
                    if (o instanceof Component c) components.add(c);
                    else components.add(Component.literal(o.toString()));
                }
                return components;
            });

    Codec<DataSource> DATA_SOURCE = ExtraRegistries.DATA_SOURCE_TYPES.byNameCodec().dispatchStable(IKapLibDataSource::codecFromVanilla, Function.identity());

    Codec<AttributeModifier> ATTRIBUTE_MODIFIER = ExtraRegistries.ATTRIBUTE_MODIFIER_TYPES.byNameCodec().dispatchStable(IKapLibAttributeModifier::codecFromVanilla, Function.identity());

    /**
     * style mixin serializing custom glyph effects
     * <br> due to style being loaded before registries are, it can't be implemented into the style codec itself
     */
    Codec<Style> EFFECT_SERIALIZING_STYLE = RecordCodecBuilder.create(styleInstance -> styleInstance.group(
            TextColor.CODEC.optionalFieldOf("color").forGetter((style) -> Optional.ofNullable(style.color)),
            Codec.BOOL.optionalFieldOf("bold", false).forGetter(Style::isBold),
            Codec.BOOL.optionalFieldOf("italic", false).forGetter(Style::isItalic),
            Codec.BOOL.optionalFieldOf("underlined", false).forGetter(Style::isUnderlined),
            Codec.BOOL.optionalFieldOf("strikethrough", false).forGetter(Style::isStrikethrough),
            Codec.BOOL.optionalFieldOf("obfuscated", false).forGetter(Style::isObfuscated),
            Codec.STRING.optionalFieldOf("insertion").forGetter((p_237269_) -> Optional.ofNullable(p_237269_.getInsertion())),
            ResourceLocation.CODEC.optionalFieldOf("font", Style.DEFAULT_FONT).forGetter(Style::getFont),
            ExtraRegistries.GLYPH_EFFECTS.byNameCodec().listOf().optionalFieldOf("effects", List.of()).forGetter(style -> List.of(EffectsStyle.of(style).getEffects()))
    ).apply(styleInstance, ExtraCodecs::createStyleFromCodec));

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static Style createStyleFromCodec(Optional<TextColor> color, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, Optional<String> insertion, ResourceLocation font, List<GlyphEffect> effects) {
        Style style = new Style(color.orElse(null), bold, italic, underlined, strikethrough, obfuscated, null, null, insertion.orElse(null), font);
        EffectsStyle.of(style).setEffects(effects.toArray(GlyphEffect[]::new));
        return style;
    }

    /**
     * @deprecated use {@link UUIDUtil#STRING_CODEC} instead
     */
    @Deprecated(forRemoval = true, since = "1.26.4")
    Codec<UUID> UUID = UUIDUtil.STRING_CODEC;

    Codec<MobEffectInstance> EFFECT = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(MobEffectInstance::getEffect),
            Codec.INT.optionalFieldOf("duration", 0).forGetter(MobEffectInstance::getDuration),
            Codec.INT.optionalFieldOf("amplifier", 0).forGetter(MobEffectInstance::getAmplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance::isAmbient),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(MobEffectInstance::isVisible)
    ).apply(instance, MobEffectInstance::new));

    Codec<Bonus<?>> BONUS = ExtraRegistries.BONUS_SERIALIZER.byNameCodec().dispatchStable(Bonus::getSerializer, RegistrySerializer::codec);
}
