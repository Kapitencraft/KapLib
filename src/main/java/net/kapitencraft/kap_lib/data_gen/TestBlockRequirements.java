package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.requirement.conditions.StatReqCondition;
import net.kapitencraft.kap_lib.requirement.datagen.RequirementProvider;
import net.kapitencraft.kap_lib.requirement.type.RequirementType;
import net.minecraft.data.PackOutput;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class TestBlockRequirements extends RequirementProvider<Block> {
    protected TestBlockRequirements(PackOutput output) {
        super(output, "test", RequirementType.BLOCK);
    }

    @Override
    protected void register() {
        this.add(Blocks.FURNACE, new StatReqCondition(Stats.BLOCK_MINED.get(Blocks.COAL_ORE), 20));
    }
}
