package net.kapitencraft.kap_lib.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.kapitencraft.kap_lib.item.entity.fishing.AbstractFishingHook;
import net.kapitencraft.kap_lib.item.entity.fishing.IFishingHook;
import net.kapitencraft.kap_lib.item.entity.item.NoFireItemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

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

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    @Shadow
    public int luck;

    protected FishingHookMixin(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"))
    public ResourceKey<LootTable> modifyLocation(ResourceKey<LootTable> id) {
        return lootTableId();
    }

    @WrapOperation(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 0))
    public boolean add(Level instance, Entity entity, Operation<Boolean> original) {
        if (self() instanceof AbstractFishingHook) {
            ItemEntity item = (ItemEntity) entity;
            return original.call(instance, NoFireItemEntity.copy(item));
        }
        return original.call(instance, entity);
    }

    @WrapOperation(method = "retrieve", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;DDDI)Lnet/minecraft/world/entity/ExperienceOrb;"))
    private ExperienceOrb modifyExperience(Level level, double x, double y, double z, int value, Operation<ExperienceOrb> original) {
        Player player = this.getPlayerOwner();
        if (player != null) value = (int) (value * ExtraAttributes.getExperienceScale(player));
        return original.call(level, x, y, z, value);
    }

    @ModifyArg(method = {"tick", "getOpenWaterTypeForBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
    public TagKey<Fluid> isProxy(TagKey<Fluid> key) {
        return getFluidType();
    }

    @WrapOperation(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    public boolean changeMaterial(BlockState instance, Block block, Operation<Boolean> original) {
        return original.call(instance, getBlock());
    }

    @Inject(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I", ordinal = 2), cancellable = true)
    public void catchingFish(BlockPos p_37146_, CallbackInfo ci) {
        this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
        this.timeUntilLured -= this.lureSpeed * 100;
        this.timeUntilLured = Math.max(1, timeUntilLured);
        ci.cancel();
    }

    @WrapOperation(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I", ordinal = 1))
    public int getRandom(RandomSource random, int minimum, int maximum, Operation<Integer> original) {
        return original.call(random, Math.max(1, minimum - getHookSpeedModifier() * 5), Math.max(1, maximum - getHookSpeedModifier() * 15));
    }

    @WrapOperation(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    public int sendParticles(ServerLevel instance, ParticleOptions type,
                             double posX, double posY, double posZ, int particleCount,
                             double xOffset, double yOffset, double zOffset, double speed,
                             Operation<Integer> original
    ) {
        if (type == ParticleTypes.SPLASH) {
            type = this.getSplashParticle();
        } else if (type == ParticleTypes.FISHING) {
            type = this.getFishingParticle();
        } else if (type == ParticleTypes.BUBBLE) {
            type = this.getBubbleParticle();
        }
        return original.call(instance, type, posX, posY, posZ, particleCount, xOffset, yOffset, zOffset, speed);
    }
}
