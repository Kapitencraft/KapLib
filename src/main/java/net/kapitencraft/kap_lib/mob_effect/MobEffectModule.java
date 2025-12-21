package net.kapitencraft.kap_lib.mob_effect;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.mob_effect.registry.ExtraMobEffects;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(MobEffectModule.MODULE_ID)
public class MobEffectModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_mob_effect";

    public MobEffectModule(IEventBus modEventBus, ModContainer container) {
        ExtraMobEffects.REGISTRY.register(modEventBus);
    }
}
