package net.kapitencraft.kap_lib.multiblock.test.registry;

import net.kapitencraft.kap_lib.multiblock.test.multiblock.entity.TestBlockEntity;
import net.kapitencraft.kap_lib.multiblock.test.multiblock.entity.TestMultiblockStructureBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface TestBlockEntityTypes {
    DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "test");

    Supplier<BlockEntityType<TestBlockEntity>> TEST = REGISTRY.register("test", () -> BlockEntityType.Builder.of(TestBlockEntity::new, TestBlocks.MP_2_CUBED_TEST.get()).build(null));
    Supplier<BlockEntityType<TestMultiblockStructureBlockEntity>> MULTIBLOCK_STRUCTURE_TEST = REGISTRY.register("mbs_test", () -> BlockEntityType.Builder.of(TestMultiblockStructureBlockEntity::new, TestBlocks.MBS_TEST.get()).build(null));
}
