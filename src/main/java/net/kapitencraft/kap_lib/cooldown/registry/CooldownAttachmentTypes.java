package net.kapitencraft.kap_lib.cooldown.registry;

import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.inventory_page.wearable.Wearables;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public interface CooldownAttachmentTypes {
    DeferredRegister<AttachmentType<?>> REGISTRY = LibConstants.registry(NeoForgeRegistries.Keys.ATTACHMENT_TYPES);

    Supplier<AttachmentType<Cooldowns>> COOLDOWNS = REGISTRY.register("cooldowns", () ->
            AttachmentType.builder(Cooldowns::new).serialize(Cooldowns.CODEC).build()
    );
}
