package net.kapitencraft.kap_lib.registry.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.ExtraCodecs;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

public interface VanillaComponentContentTypes {

    DeferredRegister<ComponentContents.Type<?>> REGISTRY = DeferredRegister.create(ExtraRegistries.Keys.COMPONENT_CONTENTS_TYPES, "minecraft");

    Supplier<ComponentContents.Type<PlainTextContents>> EMPTY = REGISTRY.register("empty", () -> new ComponentContents.Type<>(Codec.unit(PlainTextContents.EMPTY).fieldOf("value"), "empty"));

    Supplier<ComponentContents.Type<PlainTextContents>> LITERAL = REGISTRY.register("literal", () -> PlainTextContents.TYPE);

    Supplier<ComponentContents.Type<KeybindContents>> KEY_BIND = REGISTRY.register("key_bind", () -> KeybindContents.TYPE);

    Supplier<ComponentContents.Type<NbtContents>> NBT = REGISTRY.register("nbt", () -> NbtContents.TYPE);

    Supplier<ComponentContents.Type<ScoreContents>> SCORE = REGISTRY.register("score", () -> ScoreContents.TYPE);

    Supplier<ComponentContents.Type<SelectorContents>> SELECTOR = REGISTRY.register("selector", () -> SelectorContents.TYPE);

    Supplier<ComponentContents.Type<TranslatableContents>> TRANSLATABLE = REGISTRY.register("translatable", () -> TranslatableContents.TYPE);

    private static Codec<PlainTextContents.LiteralContents> createLiterals() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("text").forGetter(PlainTextContents.LiteralContents::text)
        ).apply(instance, PlainTextContents.LiteralContents::new));
    }

    private static Codec<KeybindContents> createKeybind() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("keybind").forGetter(KeybindContents::getName)
        ).apply(instance, KeybindContents::new));
    }

    private static Codec<NbtContents> createNBT() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("nbt").forGetter(NbtContents::getNbtPath),
                Codec.BOOL.fieldOf("interpret").forGetter(NbtContents::isInterpreting),
                ExtraCodecs.COMPONENT.optionalFieldOf("separator").forGetter(NbtContents::getSeparator),
                ExtraCodecs.DATA_SOURCE.fieldOf("data_source").forGetter(NbtContents::getDataSource)
        ).apply(instance, NbtContents::new));
    }

    private static Codec<ScoreContents> createScore() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(ScoreContents::getName),
                Codec.STRING.fieldOf("objective").forGetter(ScoreContents::getObjective)
        ).apply(instance, ScoreContents::new));
    }

    private static Codec<SelectorContents> createSelector() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("selector").forGetter(SelectorContents::getPattern),
                ExtraCodecs.COMPONENT.optionalFieldOf("separator").forGetter(SelectorContents::getSeparator)
        ).apply(instance, SelectorContents::new));
    }

    private static Codec<TranslatableContents> createTranslatable() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("translate").forGetter(TranslatableContents::getKey),
                Codec.STRING.optionalFieldOf("fallback").forGetter(c -> Optional.ofNullable(c.getFallback())),
                ExtraCodecs.TRANSLATABLE_COMPONENT_ARGS.optionalFieldOf("with", TranslatableContents.NO_ARGS).forGetter(TranslatableContents::getArgs)
        ).apply(instance, (string, s, objects) -> new TranslatableContents(string, s.orElse(null), objects)));
    }
}
