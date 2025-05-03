package net.kapitencraft.kap_lib.spawn_table.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.AddEffectsFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SpawnEffectCloud extends SpawnPoolSingletonContainer {
    private final Potion potion;
    private final MobEffectInstance[] effects;
    private final int duration,
            durationOnUse,
            waitTime;
    private final Integer color;
    private final float radius, radiusOnUse, radiusPerTick;
    private final LootContext.EntityTarget owner;

    protected SpawnEffectCloud(int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions,
                               Potion potion, MobEffectInstance[] effects,
                               float radius, float radiusOnUse, float radiusPerTick,
                               int duration, int durationOnUse,
                               Integer color, int waitTime, LootContext.EntityTarget owner
    ) {
        super(pWeight, pQuality, pConditions, pFunctions);
        this.potion = potion;
        this.effects = effects;
        this.duration = duration;
        this.radius = radius;
        this.radiusOnUse = radiusOnUse;
        this.radiusPerTick = radiusPerTick;
        this.durationOnUse = durationOnUse;
        this.color = color;
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
        if (potion != null) cloud.setPotion(potion);
        else if (effects != null) {
            for (MobEffectInstance effect : effects) {
                cloud.addEffect(effect);
            }
        }
        cloud.setRadius(radius);
        cloud.setRadiusOnUse(radiusOnUse);
        cloud.setRadiusPerTick(radiusPerTick);
        cloud.setDuration(duration);
        cloud.setDurationOnUse(durationOnUse);
        cloud.setWaitTime(waitTime);
        if (color != null) cloud.setFixedColor(color);
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

    public static class Serializer extends SpawnPoolSingletonContainer.Serializer<SpawnEffectCloud> {

        @Override
        public void serializeCustom(JsonObject pObject, SpawnEffectCloud pContainer, JsonSerializationContext pConditions) {
            super.serializeCustom(pObject, pContainer, pConditions);
            if (pContainer.potion != null) pObject.addProperty("potion", Objects.requireNonNull(ForgeRegistries.POTIONS.getKey(pContainer.potion), "unknown potion: " + pContainer.potion.getName("")).toString());
            if (pContainer.effects != null) pObject.add("effects", AddEffectsFunction.EFFECT_SERIALIZER.encode(List.of(pContainer.effects)));
            if (pContainer.duration != 600) pObject.addProperty("duration", pContainer.duration);
            if (pContainer.radius != 3) pObject.addProperty("radius", pContainer.radius);
            if (pContainer.radiusOnUse != -.5f) pObject.addProperty("radiusOnUse", pContainer.radiusOnUse);
            if (pContainer.radiusPerTick != -pContainer.radius / pContainer.duration) pObject.addProperty("radiusPerTick", pContainer.radiusPerTick);
            pObject.addProperty("durationOnUse", pContainer.durationOnUse);
            if (pContainer.color != null) pObject.addProperty("color", pContainer.color);
            if (pContainer.waitTime != 20) pObject.addProperty("waitTime", pContainer.waitTime);
            if (pContainer.owner != null) pObject.add("owner", pConditions.serialize(pContainer.owner));
        }

        @Override
        protected SpawnEffectCloud deserialize(JsonObject pObject, JsonDeserializationContext pContext, int pWeight, int pQuality, LootItemCondition[] pConditions, SpawnEntityFunction[] pFunctions) {
            Potion potion = pObject.has("potion") ? Potion.byName(GsonHelper.getAsString(pObject, "potion")) : null;
            MobEffectInstance[] effects = pObject.has("effects") ? AddEffectsFunction.EFFECT_SERIALIZER.parse(pObject.get("effects")).toArray(MobEffectInstance[]::new) : null;
            int duration = GsonHelper.getAsInt(pObject, "duration", 600);
            float radius = GsonHelper.getAsFloat(pObject, "radius", 3);
            float radiusOnUse = GsonHelper.getAsFloat(pObject, "radiusOnUse", -.5f);
            float radiusPerTick = GsonHelper.getAsFloat(pObject, "radiusPerTick", -radius / duration);
            int durationOnUse = GsonHelper.getAsInt(pObject, "durationOnUse");
            Integer color = pObject.has("color") ? GsonHelper.getAsInt(pObject, "color") : null;
            int waitTime = GsonHelper.getAsInt(pObject, "waitTime", 20);
            LootContext.EntityTarget owner = pObject.has("owner") ? pContext.deserialize(pObject.get("owner"), LootContext.EntityTarget.class) : null;
            return new SpawnEffectCloud(
                    pWeight, pQuality, pConditions, pFunctions,
                    potion,
                    effects,
                    radius,
                    radiusOnUse,
                    radiusPerTick,
                    duration,
                    durationOnUse,
                    color,
                    waitTime,
                    owner
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder extends SpawnPoolSingletonContainer.Builder<Builder> {
        private Potion potion;
        private MobEffectInstance[] effects;
        private float radius = 3, radiusOnUse = -.5f, radiusPerTick = -1f / 200;
        private int duration = 600, durationOnUse;
        private Integer color;
        private int waitTime = 20;
        private LootContext.EntityTarget owner;

        public Builder setOwner(LootContext.EntityTarget target) {
            this.owner = target;
            return this;
        }

        public Builder setPotion(Potion potion) {
            this.potion = potion;
            return this;
        }

        public Builder setEffects(MobEffectInstance... effects) {
            this.effects = effects;
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
                    potion, effects,
                    radius, radiusOnUse, radiusPerTick,
                    duration, durationOnUse,
                    color, waitTime, owner
            );
        }
    }
}
