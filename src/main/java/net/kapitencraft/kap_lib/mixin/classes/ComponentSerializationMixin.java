package net.kapitencraft.kap_lib.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.component.player_head.PlayerHeadContents;
import net.kapitencraft.kap_lib.component.event.custom.RegisterComponentTypesEvent;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mixin(ComponentSerialization.class)
public abstract class ComponentSerializationMixin {

    @WrapOperation(method = "createCodec", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/ComponentSerialization;createLegacyComponentMatcher([Lnet/minecraft/util/StringRepresentable;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"))
    private static MapCodec<ComponentContents> addCustomComponentTypes(
            StringRepresentable[] types,
            Function<ComponentContents, MapCodec<? extends ComponentContents>> codecGetter,
            Function<ComponentContents, ComponentContents.Type<?>> typeGetter,
            String typeFieldName, Operation<MapCodec<ComponentContents>> original
    ) {
        List<StringRepresentable> list = new ArrayList<>(List.of(types));
        list.add(PlayerHeadContents.TYPE);
        ModLoader.postEvent(new RegisterComponentTypesEvent(list));
        return original.call(list.toArray(StringRepresentable[]::new), codecGetter, typeGetter, typeFieldName);
    }
}
