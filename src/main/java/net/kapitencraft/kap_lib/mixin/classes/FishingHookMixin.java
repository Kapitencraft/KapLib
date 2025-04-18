package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.entity.fishing.IFishingHook;
import net.kapitencraft.kap_lib.entity.fishing.ModFishingHook;
import net.kapitencraft.kap_lib.entity.item.NoFireItemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Projectile implements IFishingHook {

    @Shadow
    public int lureSpeed;

    @Unique
    private int hookSpeedModifier = 0;

    @Override
    public int getHookSpeedModifier() {
        return hookSpeedModifier;
    }

    @Override
    public void setHookSpeedModifier(int hookSpeedModifier) {
        this.hookSpeedModifier = hookSpeedModifier;
    }

    @Shadow
    private int timeUntilLured;

    protected FishingHookMixin(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootDataManager;getLootTable(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    public ResourceLocation modifyLocation(ResourceLocation id) {
        return lootTableId();
    }

    @Redirect(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 0))
    public boolean add(Level instance, Entity entity) {
        if (self() instanceof ModFishingHook) {
            ItemEntity item = (ItemEntity) entity;
            return instance.addFreshEntity(NoFireItemEntity.copy(item));
        }
        return instance.addFreshEntity(entity);
    }

    @ModifyArg(method = {"tick", "getOpenWaterTypeForBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    public TagKey<Fluid> isProxy(TagKey<Fluid> key) {
        return getFluidType();
    }

    @Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    public boolean changeMaterial(BlockState instance, Block block) {
        return instance.is(getBlock());
    }

    @Inject(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I", ordinal = 2), cancellable = true)
    public void catchingFish(BlockPos p_37146_, CallbackInfo ci) {
        this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
        this.timeUntilLured -= this.lureSpeed * 100;
        this.timeUntilLured = Math.max(1, timeUntilLured);
        ci.cancel();
    }

    @Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I", ordinal = 1))
    public int getRandom(RandomSource source, int low, int high) {
        return Mth.nextInt(source, Math.max(1, low - getHookSpeedModifier() * 5), Math.max(1, high - getHookSpeedModifier() * 15));
    }

    @Redirect(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    public int sendParticles(ServerLevel level, ParticleOptions options, double d, double d1, double d2, int i1, double d3, double d4, double d5, double d6) {
        if (options == ParticleTypes.SPLASH) {
            options = this.getSplashParticle();
        } else if (options == ParticleTypes.FISHING) {
            options = this.getFishingParticle();
        } else if (options == ParticleTypes.BUBBLE) {
            options = this.getBubbleParticle();
        }
        return level.sendParticles(options, d, d1, d2, i1, d3, d4, d5, d6);
    }
}
