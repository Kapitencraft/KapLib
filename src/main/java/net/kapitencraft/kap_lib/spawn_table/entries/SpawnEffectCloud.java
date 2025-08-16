package net.kapitencraft.kap_lib.spawn_table.entries;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SpawnEffectCloud extends SpawnPoolSingletonContainer {
    public static final MapCodec<SpawnEffectCloud> CODEC = RecordCodecBuilder.mapCodec(i -> {
        Products.P4<RecordCodecBuilder.Mu<SpawnEffectCloud>, Integer, Integer, List<LootItemCondition>, List<SpawnEntityFunction>> other = singletonFields(i);
        return i.group(
                other.t1(), //that's a little unfortunate
                other.t2(),
                other.t3(),
                other.t4(),
                Codec.either(Potion.CODEC, MobEffectInstance.CODEC.listOf()).fieldOf("effects").forGetter(f -> f.effects),
                Codec.FLOAT.fieldOf("radius").forGetter(f -> f.radius),
                Codec.FLOAT.fieldOf("radiusOnUse").forGetter(f -> f.radiusOnUse),
                Codec.FLOAT.fieldOf("radiusPerTick").forGetter(f -> f.radiusPerTick),
                Codec.INT.fieldOf("duration").forGetter(f -> f.duration),
                Codec.INT.fieldOf("durationOnUse").forGetter(f -> f.durationOnUse),
                Codec.INT.fieldOf("waitTime").forGetter(f -> f.waitTime),
                LootContext.EntityTarget.CODEC.optionalFieldOf("owner").forGetter(f -> Optional.ofNullable(f.owner))
        ).apply(i, SpawnEffectCloud::fromCodec);
    });

    private static Object fromCodec(Integer integer, Integer integer1,
                                    List<LootItemCondition> lootItemConditions, List<SpawnEntityFunction> spawnEntityFunctions,
                                    Either<Holder<Potion>, List<MobEffectInstance>> holderListEither,
                                    Float aFloat, Float aFloat1, Float aFloat2, Integer integer2, Integer integer3, Integer integer4, Optional<LootContext.EntityTarget> entityTarget) {
        return new SpawnEffectCloud(integer, integer1, lootItemConditions, spawnEntityFunctions, holderListEither, aFloat, aFloat1, aFloat2, integer2, integer3, integer4, entityTarget.orElse(null));
    }

    private final Either<Holder<Potion>, List<MobEffectInstance>> effects;
    private final float radius, radiusOnUse, radiusPerTick;
    private final int duration,
            durationOnUse,
            waitTime;
    @Nullable
    private final LootContext.EntityTarget owner;

    protected SpawnEffectCloud(int pWeight, int pQuality, List<LootItemCondition> pConditions, List<SpawnEntityFunction> pFunctions,
                               Either<Holder<Potion>, List<MobEffectInstance>> effects,
                               float radius, float radiusOnUse, float radiusPerTick,
                               int duration, int durationOnUse,
                               int waitTime, LootContext.EntityTarget owner
    ) {
        super(pWeight, pQuality, pConditions, pFunctions);
        this.effects = effects;
        this.duration = duration;
        this.radius = radius;
        this.radiusOnUse = radiusOnUse;
        this.radiusPerTick = radiusPerTick;
        this.durationOnUse = durationOnUse;
        this.waitTime = waitTime;
        this.owner = owner;
    }

    @Override
    protected void createEntity(Consumer<Entity> pEntityConsumer, SpawnContext pLootContext) {
        AreaEffectCloud cloud = EntityType.AREA_EFFECT_CLOUD.create(pLootContext.getLevel());
        if (cloud == null) {
            KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "unable to create effect cloud!");
            return;
        }
        effects
                .ifLeft(p -> cloud.setPotionContents(new PotionContents(p)))
                .ifRight(e -> e.forEach(cloud::addEffect));
        cloud.setRadius(radius);
        cloud.setRadiusOnUse(radiusOnUse);
        cloud.setRadiusPerTick(radiusPerTick);
        cloud.setDuration(duration);
        cloud.setDurationOnUse(durationOnUse);
        cloud.setWaitTime(waitTime);
        if (owner != null) {
            if (pLootContext.getParam(owner.getParam()) instanceof LivingEntity living) cloud.setOwner(living);
            else KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "owner {} was no living entity", pLootContext.getParam(owner.getParam()));
        }
        pEntityConsumer.accept(cloud);
    }

    @Override
    public SpawnPoolEntryType getType() {
        return SpawnPoolEntries.EFFECT_CLOUD.get();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpawnPoolSingletonContainer.Builder<Builder> {
        private Either<Holder<Potion>, List<MobEffectInstance>> effects;
        private float radius = 3, radiusOnUse = -.5f, radiusPerTick = -1f / 200;
        private int duration = 600, durationOnUse;
        private Integer color;
        private int waitTime = 20;
        private LootContext.EntityTarget owner;

        public Builder setOwner(LootContext.EntityTarget target) {
            this.owner = target;
            return this;
        }

        public Builder setPotion(Holder<Potion> potion) {
            this.effects = Either.left(potion);
            return this;
        }

        public Builder setEffects(MobEffectInstance... effects) {
            this.effects = Either.right(List.of(effects));
            return this;
        }

        public Builder setRadius(float radius) {
            this.radius = radius;
            return this;
        }

        public Builder setRadiusOnUse(float radiusOnUse) {
            this.radiusOnUse = radiusOnUse;
            return this;
        }

        public Builder setRadiusPerTick(float radiusPerTick) {
            this.radiusPerTick = radiusPerTick;
            return this;
        }

        public Builder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder setDurationOnUse(int durationOnUse) {
            this.durationOnUse = durationOnUse;
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setColor(Color color) {
            return this.setColor(color.pack());
        }

        public Builder setWaitTime(int waitTime) {
            this.waitTime = waitTime;
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnPoolEntryContainer build() {
            return new SpawnEffectCloud(
                    weight,
                    quality,
                    getConditions(),
                    getFunctions(),
                    effects,
                    radius, radiusOnUse, radiusPerTick,
                    duration, durationOnUse,
                    waitTime, owner
            );
        }
    }
}
