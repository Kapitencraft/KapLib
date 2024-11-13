package net.kapitencraft.kap_lib.cooldown;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Cooldown {

    private final CompoundPath path;
    private final int defaultTime;
    private final Consumer<LivingEntity> toDo;

    public Cooldown(CompoundPath.Builder path, int defaultTime, Consumer<LivingEntity> toDo) {
        this.path = path.withParentIfNull(CompoundPath.COOLDOWN).build();
        this.defaultTime = defaultTime;
        this.toDo = toDo;
    }

    public void applyCooldown(LivingEntity living, boolean reduceWithTime) {
        CompoundTag tag = getTag(living);
        double mul = reduceWithTime ? living.getAttributeValue(ExtraAttributes.COOLDOWN_REDUCTION.get()) : 0;
        int value = (int) (defaultTime * (1 - mul / 100));
        if (value > 0) {
            tag.putInt(path.path(), value);
            cast(living).addCooldown(this);
        }
    }

    private static ICooldownable cast(LivingEntity living) {
        return (ICooldownable) living;
    }

    public int getCooldownTime(Entity living) {
        CompoundTag tag = getTag(living);
        return tag.getInt(path.path());
    }

    public boolean isActive(Entity entity) {
        return getCooldownTime(entity) > 0;
    }

    public void onDone(LivingEntity living) {
        toDo.accept(living);
    }

    public CompoundPath getPath() {
        return path.parent();
    }

    public String getId() {
        return path.path();
    }

    public @NotNull CompoundTag getTag(Entity entity) {
        return path.parent().getOrCreateTag(entity);
    }

    public Component createDisplay(Entity entity) {
        int cooldownTicks = getCooldownTime(entity);
        int defaultTime = entity instanceof LivingEntity living ? MathHelper.cooldown(living, this.defaultTime) : this.defaultTime;
        return Component.literal("Cooldown: " + (cooldownTicks > 0 ? TextHelper.wrapInRed("ACTIVE ") + "(" + MathHelper.round(cooldownTicks / 20., 1) + "s)" : "§aINACTIVE§r, " + MathHelper.round(defaultTime / 20., 1) + "s")).withStyle(ChatFormatting.DARK_GRAY);
    }
}
