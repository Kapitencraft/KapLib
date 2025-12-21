package net.kapitencraft.kap_lib.inventory_page.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.inventory_page.wearable.Wearables;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public interface WearableAttachmentTypes {
    DeferredRegister<AttachmentType<?>> REGISTRY = LibConstants.registry(NeoForgeRegistries.Keys.ATTACHMENT_TYPES);

    Supplier<AttachmentType<Wearables>> WEARABLES = REGISTRY.register("wearables", () ->
            AttachmentType.builder(Wearables::new).serialize(Wearables.CODEC).build()
    );
}
