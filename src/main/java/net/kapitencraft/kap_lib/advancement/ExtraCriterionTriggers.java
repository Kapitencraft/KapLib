package net.kapitencraft.kap_lib.advancement;

import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ExtraCriterionTriggers {
    DeferredRegister<CriterionTrigger<?>> REGISTRY = KapLibMod.registry(Registries.TRIGGER_TYPE);

    Supplier<ManaConsumedTrigger> MANA_CONSUMED = REGISTRY.register("mana_consumed", ManaConsumedTrigger::new);
}
