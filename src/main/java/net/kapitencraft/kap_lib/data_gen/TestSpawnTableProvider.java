package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.spawn_table.SpawnTableServerTestCommand;
import net.kapitencraft.kap_lib.spawn_table.datagen.SpawnTableProvider;
import net.kapitencraft.kap_lib.spawn_table.SpawnPool;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnEntity;
import net.kapitencraft.kap_lib.spawn_table.functions.*;
import net.kapitencraft.kap_lib.spawn_table.registry.SpawnTableRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class TestSpawnTableProvider extends SpawnTableProvider {

    public TestSpawnTableProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(pOutput, Set.of(SpawnTableServerTestCommand.TEST), List.of(
                new SubProviderEntry(TestSubProvider::new, LootContextParamSets.COMMAND)
        ), registries);
    }

    private record TestSubProvider(HolderLookup.Provider provider) implements SubProvider {

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<SpawnTable>, SpawnTable.Builder> pOutput) {

            pOutput.accept(SpawnTableServerTestCommand.TEST, SpawnTable.spawnTable()
                    .withPool(SpawnPool.spawnPool("villager")
                            .add(SpawnEntity.spawnTableEntity(EntityType.VILLAGER))
                            .apply(VillagerPropertiesFunction.builder()
                                    .setProfession(VillagerProfession.CLERIC).setLevel(4).setType(VillagerType.TAIGA)
                            ).apply(SetMerchantTradesFunction.builder()
                                    .addOffer(new MerchantOffer(new ItemCost(Items.NETHER_STAR), new ItemStack(Items.NETHERITE_INGOT, 10), 20, 5, .3f))
                            )
                    )
            );
        }
    }
}
