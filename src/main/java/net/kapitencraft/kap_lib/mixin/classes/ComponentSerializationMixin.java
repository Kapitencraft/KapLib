package net.kapitencraft.kap_lib.mixin.classes;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.client.glyph.player_head.PlayerHeadContents;
import net.kapitencraft.kap_lib.event.custom.RegisterComponentTypesEvent;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mixin(ComponentSerialization.class)
public abstract class ComponentSerializationMixin {
    @Shadow
    public static <T extends StringRepresentable, E> MapCodec<E> createLegacyComponentMatcher(T[] types, Function<T, MapCodec<? extends E>> codecGetter, Function<E, T> typeGetter, String typeFieldName) {
        return null;
    }

    @Redirect(method = "createCodec", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/ComponentSerialization;createLegacyComponentMatcher([Lnet/minecraft/util/StringRepresentable;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"))
    private static MapCodec<ComponentContents> addCustomComponentTypes(StringRepresentable[] types, Function<StringRepresentable, MapCodec<? extends ComponentContents>> codecGetter, Function<ComponentContents, StringRepresentable> typeGetter, String typeFieldName) {
        List<StringRepresentable> list = new ArrayList<>(List.of(types));
        list.add(PlayerHeadContents.TYPE);
        ModLoader.postEvent(new RegisterComponentTypesEvent(list));
        return createLegacyComponentMatcher(list.toArray(StringRepresentable[]::new), codecGetter, typeGetter, typeFieldName);
    }
}
