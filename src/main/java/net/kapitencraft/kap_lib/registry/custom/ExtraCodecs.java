package net.kapitencraft.kap_lib.registry.custom;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.mixin.duck.IKapLibComponentContents;
import net.kapitencraft.kap_lib.mixin.duck.IKapLibDataSource;
import net.kapitencraft.kap_lib.mixin.duck.attribute.IKapLibAttributeModifier;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface ExtraCodecs {
    Codec<ComponentContents> COMPONENT_TYPES = ExtraRegistries.COMPONENT_CONTENT_TYPES.getCodec().dispatchStable(IKapLibComponentContents::codecFromVanilla, Function.identity());
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
    Codec<DataSource> DATA_SOURCE = ExtraRegistries.DATA_SOURCE_TYPES.getCodec().dispatchStable(IKapLibDataSource::codecFromVanilla, Function.identity());
    Codec<AttributeModifier> ATTRIBUTE_MODIFIER = ExtraRegistries.ATTRIBUTE_MODIFIER_TYPES.getCodec().dispatchStable(IKapLibAttributeModifier::codecFromVanilla, Function.identity());
}
