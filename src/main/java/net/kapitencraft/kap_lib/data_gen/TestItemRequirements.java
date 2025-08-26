package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.data_gen.abst.RequirementProvider;
import net.kapitencraft.kap_lib.requirements.type.RegistryReqType;
import net.kapitencraft.kap_lib.requirements.conditions.DimensionReqCondition;
import net.kapitencraft.kap_lib.requirements.conditions.StatReqCondition;
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
        super(output, KapLibMod.MOD_ID, RegistryReqType.ITEM);
    }

    @Override
    protected void register() {
        this.add(Items.ELYTRA, new StatReqCondition(Stats.ENTITY_KILLED.get(EntityType.ENDER_DRAGON), 5));
        this.add(Items.NETHERITE_SWORD, new DimensionReqCondition(Level.NETHER));
    }
}
