package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class VillagerPropertiesFunction extends SpawnEntityConditionalFunction {
    private final @Nullable VillagerType type;
    private final @Nullable VillagerProfession profession;
    private final @Nullable Integer level;

    protected VillagerPropertiesFunction(LootItemCondition[] pPredicates, @Nullable VillagerType type, @Nullable VillagerProfession profession, @Nullable Integer level) {
        super(pPredicates);
        this.type = type;
        this.profession = profession;
        this.level = level;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof Villager villager) {
            VillagerData data = villager.getVillagerData();
            if (type != null) data = data.setType(type);
            if (profession != null) data = data.setProfession(profession);
            if (level != null) data = data.setLevel(level);
            villager.setVillagerData(data);
        }
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.VILLAGER_PROPERTIES.get();
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<VillagerPropertiesFunction> {

        @Override
        public void serialize(JsonObject pJson, VillagerPropertiesFunction pFunction, JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            if (pFunction.type != null) pJson.addProperty("biome_type", BuiltInRegistries.VILLAGER_TYPE.getKey(pFunction.type).toString());
            if (pFunction.profession != null) JsonHelper.addRegistryElement(pJson, "profession", pFunction.profession, ForgeRegistries.VILLAGER_PROFESSIONS);
            JsonHelper.addOptionalInt(pJson, "level", pFunction.level);
        }

        @Override
        public VillagerPropertiesFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            VillagerType type = pObject.has("biome_type") ? BuiltInRegistries.VILLAGER_TYPE.get(new ResourceLocation(GsonHelper.getAsString(pObject, "biome_type"))) : null;
            VillagerProfession profession = pObject.has("profession") ? JsonHelper.getAsRegistryElement(pObject, "profession", ForgeRegistries.VILLAGER_PROFESSIONS) : null;
            Integer level = JsonHelper.getAsOptionalInt(pObject, "level");
            return new VillagerPropertiesFunction(pConditions, type, profession, level);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private VillagerType type;
        private VillagerProfession profession;
        private Integer level;

        public Builder setType(VillagerType type) {
            this.type = type;
            return this;
        }

        public Builder setProfession(VillagerProfession profession) {
            this.profession = profession;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public SpawnEntityFunction build() {
            return new VillagerPropertiesFunction(getConditions(), type, profession, level);
        }
    }
}
