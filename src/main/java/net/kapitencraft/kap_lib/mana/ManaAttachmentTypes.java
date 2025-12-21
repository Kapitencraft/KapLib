package net.kapitencraft.kap_lib.mana;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * for accessing mana, use {@link ManaHandler#getMana(LivingEntity)} and {@link ManaHandler#setMana(LivingEntity, double)} respectively
 */
@ApiStatus.Internal
public interface ManaAttachmentTypes {
    DeferredRegister<AttachmentType<?>> REGISTRY = LibConstants.registry(NeoForgeRegistries.Keys.ATTACHMENT_TYPES);

    /**
     * use {@link ManaHandler#getMana(LivingEntity) ManaHandler#getMana} and {@link ManaHandler#setMana(LivingEntity, double) ManaHandler#setMana} instead
     */
    Supplier<AttachmentType<Double>> MANA = REGISTRY.register("mana", () ->
            AttachmentType.builder(() -> 0d).serialize(Codec.DOUBLE).sync(ByteBufCodecs.DOUBLE).build());

}
