package net.kapitencraft.kap_lib.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NoFireItemEntity extends ItemEntity {
    public NoFireItemEntity(Level level, double x, double y, double z, ItemStack item) {
        super(level, x, y, z, item);
    }

    public static NoFireItemEntity copy(ItemEntity origin) {
        NoFireItemEntity noFireItemEntity = new NoFireItemEntity(origin.level(), origin.getX(), origin.getY(), origin.getZ(), origin.getItem());
        noFireItemEntity.setDeltaMovement(origin.getDeltaMovement());
        return noFireItemEntity;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}
