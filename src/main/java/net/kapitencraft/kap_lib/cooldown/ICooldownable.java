package net.kapitencraft.kap_lib.cooldown;

import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICooldownable extends IForgeLivingEntity {
    @NotNull List<Cooldown> getActiveCooldowns();

    default void addCooldown(Cooldown cooldown) {
        getActiveCooldowns().add(cooldown);
    }

    default void tickCooldowns() {
        getActiveCooldowns().removeIf(cooldown -> {
            CompoundPath path = cooldown.getPath();
            CompoundTag tag = path.getTag(self());
            if (tag != null) {
                String tagName = cooldown.getId();
                if (tag.contains(tagName, 3) && tag.getInt(tagName) > 0) {
                    IOHelper.reduceBy1(tag, tagName);
                    if (tag.getInt(tagName) <= 0) {
                        tag.remove(tagName);
                        cooldown.onDone(self());
                        return true;
                    }
                }
            }
            return false;
        });
    }

    static ICooldownable of(LivingEntity living) {
        return (ICooldownable) living;
    }
}
