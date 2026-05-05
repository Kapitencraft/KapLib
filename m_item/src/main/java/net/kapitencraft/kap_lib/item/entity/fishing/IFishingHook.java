package net.kapitencraft.kap_lib.item.entity.fishing;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public interface IFishingHook {

    default FishingHook self() {
        return (FishingHook) this;
    }

    /**
     * provides the fluid type the fishing hook works in
     * <br> defaults to water
     * @return the fluid type
     */
    default TagKey<Fluid> getFluidType() {
        return FluidTags.WATER;
    }

    /**
     * sets the hook speed modifier, which reduces the time it takes to hook a fish
     * @param modifier the modifier. values above 26 instantly hook fish
     */
    void setHookSpeedModifier(int modifier);

    /**
     * provides the hook speed modifier
     * @return the hook speed modifier
     */
    int getHookSpeedModifier();

    /**
     * provides the loot table to get loot from when the fishing completes
     * @return the loot table key
     */
    default ResourceKey<LootTable> lootTableId() {
        return BuiltInLootTables.FISHING;
    }

    default Block getBlock() {
        return Blocks.WATER;
    }

    /**
     * provides the bubbling particle that spawn during hooking
     * @return the bubbling particle
     */
    default ParticleOptions getBubbleParticle() {
        return ParticleTypes.BUBBLE;
    }

    /**
     * provides the fishing particle that spawns during hooking
     * @return the fishing particle
     */
    default ParticleOptions getFishingParticle() {
        return ParticleTypes.FISHING;
    }

    /**
     * provides the splash particle that spawns during luring
     * @return the splash particle
     */
    default ParticleOptions getSplashParticle() {
        return ParticleTypes.SPLASH;
    }
}
