package net.kapitencraft.kap_lib.entity.fishing;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFishingHook extends FishingHook implements IFishingHook {
    private TagKey<Fluid> fluidType;
    private int hookSpeedModifier;

    private AbstractFishingHook(EntityType<? extends AbstractFishingHook> type, Level level, int luck, int lureSpeed, TagKey<Fluid> fluidType) {
        super(type, level, luck, lureSpeed);
        this.fluidType = fluidType;
    }

    protected AbstractFishingHook(EntityType<? extends AbstractFishingHook> type, Level level, TagKey<Fluid> fluidType) {
        this(type, level, 0, 0, fluidType);
    }


    protected AbstractFishingHook(EntityType<? extends AbstractFishingHook> type, Player player, Level level, int luck, int lureSpeed, TagKey<Fluid> fluidType) {
        this(type, level, luck, lureSpeed, fluidType);
        this.setOwner(player);
        float f = player.getXRot();
        float f1 = player.getYRot();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        double d0 = player.getX() - (double)f3 * 0.3D;
        double d1 = player.getEyeY();
        double d2 = player.getZ() - (double)f2 * 0.3D;
        this.moveTo(d0, d1, d2, f1, f);
        Vec3 vec3 = new Vec3(-f3, Mth.clamp(-(f5 / f4), -5.0F, 5.0F), -f2);
        double d3 = vec3.length();
        vec3 = vec3.multiply(0.6D / d3 + this.random.triangle(0.5D, 0.0103365D), 0.6D / d3 + this.random.triangle(0.5D, 0.0103365D), 0.6D / d3 + this.random.triangle(0.5D, 0.0103365D));
        this.setDeltaMovement(vec3);
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, vec3.horizontalDistance()) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putString("fluidType", this.fluidType.location().toString());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        this.fluidType = TagKey.create(Registries.FLUID, new ResourceLocation(tag.getString("fluidType")));
    }

    @Override
    public TagKey<Fluid> getFluidType() {
        return this.fluidType;
    }

    @Override
    public int getHookSpeedModifier() {
        return hookSpeedModifier;
    }

    @Override
    public void setHookSpeedModifier(int hookSpeedModifier) {
        this.hookSpeedModifier = hookSpeedModifier;
    }
}