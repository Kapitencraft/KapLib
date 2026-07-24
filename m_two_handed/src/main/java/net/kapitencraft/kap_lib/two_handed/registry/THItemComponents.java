package net.kapitencraft.kap_lib.two_handed.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.two_handed.TwoHanded;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface THItemComponents {
    DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LibConstants.MOD_ID);

    Supplier<DataComponentType<TwoHanded>> TWO_HANDED = REGISTRY.registerComponentType("two_handed", b -> b.persistent(TwoHanded.CODEC).networkSynchronized(TwoHanded.STREAM_CODEC));
}