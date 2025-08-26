package net.kapitencraft.kap_lib.entity.item;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UnCollectableItemEntity extends ItemEntity {
    private List<UUID> collectors;
    private boolean moveTowards;

    public UnCollectableItemEntity(Level p_32001_, double p_32002_, double p_32003_, double p_32004_, ItemStack p_32005_) {
        super(p_32001_, p_32002_, p_32003_, p_32004_, p_32005_);
    }

    public void addPlayer(Player player) {
        collectors.add(player.getUUID());
    }

    public void move() {
        this.moveTowards = true;
    }

    @Override
    public void tick() {
        if (moveTowards && this.level() instanceof ServerLevel serverLevel) {
            List<Entity> targets = collectors.stream().map(serverLevel::getEntity).filter(Objects::nonNull).sorted(Comparator.comparingDouble(value -> value.distanceTo(this))).toList();
            if (targets.size() > 0) {
                Entity target0 = targets.get(0);
                Vec3 offSet = target0.position().subtract(this.position());
                this.move(MoverType.SELF, MathHelper.clampLength(offSet, 5));
            }
        }
    }

    @Override
    public void playerTouch(@NotNull Player p_32040_) {
        if (collectors.contains(p_32040_.getUUID())) super.playerTouch(p_32040_);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.collectors = tag.getList("Collectors", Tag.TAG_INT_ARRAY).stream().map(NbtUtils::loadUUID).toList();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag listTag = new ListTag();
        this.collectors.stream().map(NbtUtils::createUUID).forEach(listTag::add);
        tag.put("Collectors", listTag);
    }
}