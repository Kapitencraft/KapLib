package net.kapitencraft.kap_lib.entity.fishing;

import net.minecraft.client.resources.model.Material;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public interface IFishingHook {

    default FishingHook self() {
        return (FishingHook) this;
    }

    default TagKey<Fluid> getFluidType() {
        return FluidTags.WATER;
    }

    void setHookSpeedModifier(int modifier);
    int getHookSpeedModifier();

    default ResourceLocation lootTableId() {
        return BuiltInLootTables.FISHING;
    }

    default Block getBlock() {
        return Blocks.WATER;
    }

    default ParticleOptions getBubbleParticle() {
        return ParticleTypes.BUBBLE;
    }
    default ParticleOptions getFishingParticle() {
        return ParticleTypes.FISHING;
    }
    default ParticleOptions getSplashParticle() {
        return ParticleTypes.SPLASH;
    }
}
