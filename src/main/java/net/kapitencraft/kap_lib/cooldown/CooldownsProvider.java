package net.kapitencraft.kap_lib.cooldown;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class CooldownsProvider implements ICapabilityProvider, INBTSerializable<Tag> {
    private static final Codec<Map<Cooldown, Integer>> CODEC = Codec.unboundedMap(ExtraRegistries.COOLDOWNS.byNameCodec(), Codec.INT);

    private final Cooldowns cooldowns;

    public CooldownsProvider(LivingEntity living) {
        this.cooldowns = new Cooldowns(living);
    }

    @Override
    public @Nullable Object getCapability(Object object, Object context) {
        return null;
    }

    @Override
    public @UnknownNullability Tag serializeNBT(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
    }
}
