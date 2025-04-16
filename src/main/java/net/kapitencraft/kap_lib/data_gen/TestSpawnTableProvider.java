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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
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
                    .withPool(SpawnPool.spawnPool("zombie")
                            .add(SpawnEntity.spawnTableEntity(EntityType.ZOMBIE))
                            .apply(SetArmorFunction.builder().withItem(EquipmentSlot.CHEST, Items.NETHERITE_CHESTPLATE))
                            .apply(SetAttributesFunction.builder()
                                    .withAttribute(Attributes.ATTACK_DAMAGE).setBase(100).end()
                            )
                    )
            );
        }
    }
}
