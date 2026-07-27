package net.kapitencraft.kap_lib.attribute;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.attribute.timed.TimedModifiers;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * registry entry for timed modifiers.
 * <br> internal. use {@link net.kapitencraft.kap_lib.attribute.timed.TimedModifierUtils TimedModifierUtils} instead
 */
@ApiStatus.Internal
public interface AttributeAttachmentTypes {

    /**
     * internal
     */
    DeferredRegister<AttachmentType<?>> REGISTRY = LibConstants.registry(NeoForgeRegistries.Keys.ATTACHMENT_TYPES);

    /**
     * internal
     */
    Supplier<AttachmentType<TimedModifiers>> TIMED_MODIFIERS = REGISTRY.register("timed_modifiers", () ->
            AttachmentType.builder(TimedModifiers::new).serialize(TimedModifiers.CODEC).build()
    );
    Supplier<AttachmentType<Integer>> DOUBLE_JUMPS = REGISTRY.register("double_jumps", () ->
            AttachmentType.builder(() -> 0).serialize(Codec.INT).sync(ByteBufCodecs.VAR_INT).build()
    );
}
