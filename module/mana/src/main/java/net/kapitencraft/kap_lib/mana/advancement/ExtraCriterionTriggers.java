package net.kapitencraft.kap_lib.mana.advancement;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ExtraCriterionTriggers {
    DeferredRegister<CriterionTrigger<?>> REGISTRY = LibConstants.registry(Registries.TRIGGER_TYPE);

    Supplier<ManaConsumedTrigger> MANA_CONSUMED = REGISTRY.register("mana_consumed", ManaConsumedTrigger::new);
}
