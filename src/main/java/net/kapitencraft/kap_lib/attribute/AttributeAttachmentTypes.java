package net.kapitencraft.kap_lib.attribute;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public interface AttributeAttachmentTypes {

    DeferredRegister<AttachmentType<?>> REGISTRY = LibConstants.registry(NeoForgeRegistries.Keys.ATTACHMENT_TYPES);

    Supplier<AttachmentType<TimedModifiers>> TIMED_MODIFIERS = REGISTRY.register("timed_modifiers", () ->
            AttachmentType.builder(TimedModifiers::new).serialize(TimedModifiers.CODEC).build()
    );
}
