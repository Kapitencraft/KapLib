package net.kapitencraft.kap_lib.loot.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.loot.registry.ExtraLootItemConditions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TagKeyCondition implements LootItemCondition {
    private static final TagKeyCondition EMPTY = new TagKeyCondition(null, "", null);

    public static final MapCodec<TagKeyCondition> CODEC = RecordCodecBuilder.mapCodec(tagKeyConditionInstance ->
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
        return ExtraLootItemConditions.TAG_KEY.value();
    }

    @SuppressWarnings("ALL")
    @Override
    public boolean test(LootContext context) {
        if (this == EMPTY) return false;
        switch (this.type) {
            case ENTITY, ITEM -> {
                Entity entity = context.getParam(target.getParam());
                return entity != null &&
                        type == Type.ENTITY ?
                        entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(id))) :
                        entity instanceof LivingEntity living &&
                                living.getMainHandItem().is(TagKey.create(Registries.ITEM, ResourceLocation.parse(id)));
            }
            case BLOCK -> {
                BlockState state = context.getParam(LootContextParams.BLOCK_STATE);
                return state != null && state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse(id)));
            }
        }
        return false;
    }

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