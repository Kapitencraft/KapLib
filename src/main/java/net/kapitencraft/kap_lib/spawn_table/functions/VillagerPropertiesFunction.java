package net.kapitencraft.kap_lib.spawn_table.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class VillagerPropertiesFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<VillagerPropertiesFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BuiltInRegistries.VILLAGER_TYPE.byNameCodec().optionalFieldOf("biomeType").forGetter(f -> Optional.ofNullable(f.type)),
            BuiltInRegistries.VILLAGER_PROFESSION.byNameCodec().optionalFieldOf("profession").forGetter(f -> Optional.ofNullable(f.profession)),
            Codec.INT.optionalFieldOf("level", 0).forGetter(f -> f.level)
            ).and(commonFields(i).t1()).apply(i, VillagerPropertiesFunction::fromCodec));

    private static VillagerPropertiesFunction fromCodec(Optional<VillagerType> villagerType, Optional<VillagerProfession> villagerProfession, int level, List<LootItemCondition> lootItemConditions) {
        return new VillagerPropertiesFunction(lootItemConditions, villagerType.orElse(null), villagerProfession.orElse(null), level);
    }

    private final @Nullable VillagerType type;
    private final @Nullable VillagerProfession profession;
    private final @Nullable Integer level;

    protected VillagerPropertiesFunction(List<LootItemCondition> pPredicates, @Nullable VillagerType type, @Nullable VillagerProfession profession, @Nullable Integer level) {
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
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.VILLAGER_PROPERTIES.get();
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
