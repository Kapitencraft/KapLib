package net.kapitencraft.kap_lib.item.loot_table.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.ExtraLootItemConditions;
import net.kapitencraft.kap_lib.util.Reference;
import net.kapitencraft.kap_lib.item.loot_table.LootContextReader;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TagKeyCondition extends BaseCondition {
    private static final TagKeyCondition EMPTY = new TagKeyCondition(null, "", null);

    public static final Codec<TagKeyCondition> CODEC = RecordCodecBuilder.create(tagKeyConditionInstance ->
            tagKeyConditionInstance.group(
                    Type.CODEC.fieldOf("type").forGetter(TagKeyCondition::type),
                    Codec.STRING.fieldOf("id").forGetter(TagKeyCondition::getId),
                    Codec.STRING.optionalFieldOf("target").forGetter(TagKeyCondition::makeCodecTarget)
            ).apply(tagKeyConditionInstance, TagKeyCondition::fromCodec)
    );

    private Optional<String> makeCodecTarget() {
        return target == null ? Optional.empty() : Optional.of(target.getName());
    }

    private final String id;
    private final Type type;
    private final @Nullable LootContext.EntityTarget target;

    public static TagKeyCondition fromCodec(Type type, String tagId, Optional<String> entityTarget) {
        return new TagKeyCondition(type, tagId, of(entityTarget));
    }

    private static @Nullable LootContext.EntityTarget of(Optional<String> optional) {
        return optional.map(LootContext.EntityTarget::getByName).orElse(null);
    }

    public TagKeyCondition(Type type, String tagId, LootContext.@Nullable EntityTarget target) {
        this.type = type;
        this.id = tagId;
        this.target = target;
    }

    public Type type() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return ExtraLootItemConditions.TAG_KEY.get();
    }

    @SuppressWarnings("ALL")
    @Override
    public boolean test(LootContext context) {
        if (this == EMPTY) return false;
        Reference<Boolean> reference = Reference.of(false);
        switch (this.type) {
            case ENTITY, ITEM -> {
                LootContextReader.simple(context, Entity.class, (LootContextParam<Entity>) target.getParam()).ifPresent(entity -> {
                    if (type == Type.ENTITY) reference.setValue(entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(id))));
                    else reference.setValue(entity instanceof LivingEntity living ? living.getMainHandItem().is(TagKey.create(Registries.ITEM, new ResourceLocation(id))) : false);
                });
            }
            case BLOCK -> {
                LootContextReader.simple(context, BlockState.class, LootContextParams.BLOCK_STATE).ifPresent(state -> reference.setValue(state.is(TagKey.create(Registries.BLOCK, new ResourceLocation(id)))));
            }
        }
        return reference.getValue();
    }

    public static final JsonSerializer<TagKeyCondition> SERIALIZER = new JsonSerializer<>(CODEC, ()-> EMPTY);

    public enum Type implements StringRepresentable {
        ENTITY("entities"),
        BLOCK("blocks"),
        ITEM("items"),
        EMPTY("empty");
        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        public boolean is(LootContext context) {
            return context.getQueriedLootTableId().getPath().contains(name);
        }

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}