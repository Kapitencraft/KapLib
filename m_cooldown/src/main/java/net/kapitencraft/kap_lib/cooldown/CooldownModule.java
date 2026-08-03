package net.kapitencraft.kap_lib.cooldown;

import net.kapitencraft.kap_lib.cooldown.registry.CooldownAttachmentTypes;
import net.kapitencraft.kap_lib.cooldown.registry.CooldownAttributes;
import net.kapitencraft.kap_lib.cooldown.registry.CooldownLootItemConditions;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(CooldownModule.MODULE_ID)
public class CooldownModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_cooldown";

    public CooldownModule(IEventBus modEventBus) {
        CooldownAttributes.REGISTRY.register(modEventBus);
        CooldownAttachmentTypes.REGISTRY.register(modEventBus);
        CooldownLootItemConditions.REGISTRY.register(modEventBus);
    }
}
