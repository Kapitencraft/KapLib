package net.kapitencraft.kap_lib.registry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.kapitencraft.kap_lib.inventory.wearable.Wearables;
import net.kapitencraft.kap_lib.util.attribute.TimedModifiers;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public interface ModAttachmentTypes {
    DeferredRegister<AttachmentType<?>> REGISTRY = KapLibMod.registry(NeoForgeRegistries.Keys.ATTACHMENT_TYPES);

    Supplier<AttachmentType<Wearables>> WEARABLES = REGISTRY.register("wearables", () ->
            AttachmentType.builder(Wearables::new).serialize(Wearables.CODEC).build()
    );
    Supplier<AttachmentType<Cooldowns>> COOLDOWNS = REGISTRY.register("cooldowns", () ->
            AttachmentType.builder(Cooldowns::new).serialize(Cooldowns.CODEC).build()
    );
    Supplier<AttachmentType<TimedModifiers>> TIMED_MODIFIERS = REGISTRY.register("timed_modifiers", () ->
            AttachmentType.builder(TimedModifiers::new).serialize(TimedModifiers.CODEC).build()
    );
    /**
     * use {@link net.kapitencraft.kap_lib.util.ManaHandler#getMana(LivingEntity) ManaHandler#getMana} and {@link net.kapitencraft.kap_lib.util.ManaHandler#setMana(LivingEntity, double) ManaHandler#setMana} instead
     */
    Supplier<AttachmentType<Double>> MANA = REGISTRY.register("mana", () ->
            AttachmentType.builder(() -> 0d).serialize(Codec.DOUBLE).sync(ByteBufCodecs.DOUBLE).build());
}
