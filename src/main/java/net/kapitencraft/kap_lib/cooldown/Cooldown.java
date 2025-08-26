package net.kapitencraft.kap_lib.cooldown;

import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class Cooldown {

    private final int defaultTime;
    private final Consumer<LivingEntity> toDo;

    public Cooldown(int defaultTime, Consumer<LivingEntity> toDo) {
        this.defaultTime = defaultTime;
        this.toDo = toDo;
    }

    public void applyCooldown(LivingEntity living, boolean reduceWithTime) {
        Cooldowns.get(living).applyCooldown(this, reduceWithTime);
    }

    public int getCooldownTime(LivingEntity living, boolean reduceWithTime) {
        double mul = reduceWithTime ? living.getAttributeValue(ExtraAttributes.COOLDOWN_REDUCTION.get()) : 0;
        return (int) (defaultTime * (1 - mul / 100));
    }

    public int getActiveCooldownTime(LivingEntity living) {
        return Cooldowns.get(living).getCooldownTime(this);
    }

    public boolean isActive(LivingEntity entity) {
        return Cooldowns.get(entity).isActive(this);
    }

    public void onDone(LivingEntity living) {
        toDo.accept(living);
    }

    public Component createDisplay(LivingEntity living) {
        int cooldownTicks = getActiveCooldownTime(living);
        int defaultTime = MathHelper.cooldown(living, this.defaultTime);
        return Component.translatable("cooldown.display", (cooldownTicks > 0 ?
                Component.translatable("cooldown.active").withStyle(ChatFormatting.RED).append(CommonComponents.SPACE).append(Component.literal("(" + MathHelper.shortRound(cooldownTicks / 20.) + "s)").withStyle(ChatFormatting.DARK_GRAY))
                : Component.translatable("cooldown.inactive").withStyle(ChatFormatting.GREEN).append(Component.literal(", " + MathHelper.shortRound(defaultTime / 20.) + "s").withStyle(ChatFormatting.DARK_GRAY))
                )
        );
    }

    /**
     * Experimental due to the fact that this may not work with Registries, especially custom ones
     */
    @ApiStatus.Experimental
    public static <T> void registerMappedCooldown(DeferredRegister<Cooldown> targetRegister, Function<T, String> function, String groupName, Consumer<LivingEntity> onExecute, Iterable<T> entries, int baseTime) {
        for (T entry : entries) {
            targetRegister.register(groupName + "/" + function.apply(entry), () -> new Cooldown(baseTime, onExecute));
        }
    }
}
