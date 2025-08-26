package net.kapitencraft.kap_lib.cooldown;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class CooldownsProvider implements ICapabilityProvider, INBTSerializable<Tag> {
    private static final Codec<Map<Cooldown, Integer>> CODEC = Codec.unboundedMap(ExtraRegistries.COOLDOWNS.getCodec(), Codec.INT);

    private final LazyOptional<Cooldowns> lazy;
    private final Cooldowns cooldowns;

    public CooldownsProvider(LivingEntity living) {
        this.cooldowns = new Cooldowns(living);
        this.lazy = LazyOptional.of(() -> this.cooldowns);
    }

    @Override
    public Tag serializeNBT() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this.cooldowns.getData()).resultOrPartial(s -> KapLibMod.LOGGER.warn("error saving cooldowns {}", s)).orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        this.cooldowns.loadData(CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(s -> KapLibMod.LOGGER.warn("error loading cooldowns")).orElse(Map.of()));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return Cooldowns.CAPABILITY.orEmpty(cap, this.lazy);
    }
}
