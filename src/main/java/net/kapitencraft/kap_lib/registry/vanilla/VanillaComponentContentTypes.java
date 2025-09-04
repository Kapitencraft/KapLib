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
}
