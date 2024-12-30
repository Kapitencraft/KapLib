package net.kapitencraft.kap_lib.registry.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface VanillaComponentContentTypes {

    DeferredRegister<Codec<? extends ComponentContents>> REGISTRY = DeferredRegister.create(ExtraRegistryKeys.COMPONENT_CONTENTS_TYPES, "minecraft");

    RegistryObject<Codec<LiteralContents>> LITERAL = REGISTRY.register("literal", VanillaComponentContentTypes::createLiterals);

    RegistryObject<Codec<KeybindContents>> KEY_BIND = REGISTRY.register("key_bind", VanillaComponentContentTypes::createKeybind);

    RegistryObject<Codec<NbtContents>> NBT = REGISTRY.register("nbt", VanillaComponentContentTypes::createNBT);

    RegistryObject<Codec<ScoreContents>> SCORE = REGISTRY.register("score", VanillaComponentContentTypes::createScore);

    RegistryObject<Codec<SelectorContents>> SELECTOR = REGISTRY.register("selector", VanillaComponentContentTypes::createSelector);

    RegistryObject<Codec<TranslatableContents>> TRANSLATABLE = REGISTRY.register("translatable", VanillaComponentContentTypes::createTranslatable);

    private static Codec<LiteralContents> createLiterals() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("text").forGetter(LiteralContents::text)
        ).apply(instance, LiteralContents::new));
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
                Codec.STRING.optionalFieldOf("fallback", null).forGetter(TranslatableContents::getFallback),
                ExtraCodecs.TRANSLATABLE_COMPONENT_ARGS.optionalFieldOf("with", TranslatableContents.NO_ARGS).forGetter(TranslatableContents::getArgs)
        ).apply(instance, TranslatableContents::new));
    }
}
