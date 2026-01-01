package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.requirement.conditions.DimensionReqCondition;
import net.kapitencraft.kap_lib.requirement.conditions.StatReqCondition;
import net.kapitencraft.kap_lib.requirement.datagen.RequirementProvider;
import net.kapitencraft.kap_lib.requirement.type.RegistryReqType;
import net.minecraft.data.PackOutput;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * example Requirement provider
 */
public class TestItemRequirements extends RequirementProvider<Item> {

    protected TestItemRequirements(PackOutput output) {
        super(output, "test", RegistryReqType.ITEM);
    }

    @Override
    protected void register() {
        this.add(Items.ELYTRA, new StatReqCondition(Stats.ENTITY_KILLED.get(EntityType.ENDER_DRAGON), 5));
        this.add(Items.NETHERITE_SWORD, new DimensionReqCondition(Level.NETHER));
    }
}
