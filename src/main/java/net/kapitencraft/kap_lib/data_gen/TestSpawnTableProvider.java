package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.data_gen.abst.SpawnTableProvider;
import net.kapitencraft.kap_lib.spawn_table.SpawnPool;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnEffectCloud;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnEntity;
import net.kapitencraft.kap_lib.spawn_table.functions.*;
import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class TestSpawnTableProvider extends SpawnTableProvider {
    public TestSpawnTableProvider(PackOutput pOutput) {
        super(pOutput, Set.of(new ResourceLocation("test:test")), List.of(
                new SubProviderEntry(TestSubProvider::new, LootContextParamSets.COMMAND)
        ));
    }

    private static class TestSubProvider implements SubProvider {

        @Override
        public void generate(BiConsumer<ResourceLocation, SpawnTable.Builder> pOutput) {
            pOutput.accept(new ResourceLocation("test:test"), SpawnTable.spawnTable()
                    .withPool(SpawnPool.spawnPool("villager")
                            .add(SpawnEntity.spawnTableEntity(EntityType.VILLAGER))
                            .apply(VillagerPropertiesFunction.builder()
                                    .setProfession(VillagerProfession.CLERIC).setLevel(4).setType(VillagerType.TAIGA)
                            ).apply(SetMerchantTradesFunction.builder()
                                    .addOffer(new MerchantOffer(new ItemStack(Items.NETHER_STAR), new ItemStack(Items.NETHERITE_INGOT, 10), 20, 5, .3f))
                            )
                    )
            );
        }
    }
}
