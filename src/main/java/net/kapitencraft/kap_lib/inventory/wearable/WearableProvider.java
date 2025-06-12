package net.kapitencraft.kap_lib.inventory.wearable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WearableProvider implements ICapabilitySerializable<ListTag> {
    private final LazyOptional<Wearables> lazy;
    private final Wearables wearable;

    public WearableProvider(LivingEntity living) {
        this.wearable = new Wearables(living);
        this.lazy = LazyOptional.of(() -> this.wearable);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return Wearables.CAPABILITY.orEmpty(cap, this.lazy);
    }

    @Override
    public ListTag serializeNBT() {
        return this.wearable.save();
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        this.wearable.load(nbt);
    }
}