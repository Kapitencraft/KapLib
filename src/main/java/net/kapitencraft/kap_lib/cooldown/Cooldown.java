package net.kapitencraft.kap_lib.cooldown;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.cooldown.registry.CooldownAttributes;
import net.kapitencraft.kap_lib.cooldown.registry.CooldownRegistries;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Function;

public class Cooldown {
    public static final Codec<Cooldown> CODEC = CooldownRegistries.COOLDOWNS.byNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, Cooldown> STREAM_CODEC = ByteBufCodecs.registry(CooldownRegistries.Keys.COOLDOWNS);

    private final int defaultTime;
    private final Consumer<LivingEntity> toDo;

    public Cooldown(int defaultTime, Consumer<LivingEntity> toDo) {
        this.defaultTime = defaultTime;
        this.toDo = toDo;
    }

    /**
     * applies or resets this cooldown on the given entity
     * @param living the entity to apply the cooldown to
     * @param reduceWithTime if the {@link CooldownAttributes#COOLDOWN_REDUCTION} attribute should be considered
     */
    public void applyCooldown(LivingEntity living, boolean reduceWithTime) {
        Cooldowns.get(living).applyCooldown(living, this, reduceWithTime);
    }

    /**
     * @param living the entity of the request
     * @param reduceWithTime whether the {@link CooldownAttributes#COOLDOWN_REDUCTION} attribute should be considered
     * @return the time in ticks the cooldown would be active
     */
    public int getCooldownTime(LivingEntity living, boolean reduceWithTime) {
        double mul = reduceWithTime ? living.getAttributeValue(CooldownAttributes.COOLDOWN_REDUCTION) : 0;
        return (int) (defaultTime * (1 - mul / 100));
    }

    /**
     * gets the remaining time of this cooldown on the given entity.
     * @param living the entity to query
     * @return the remaining time or 0 if it is not active
     */
    public int getRemainingCooldownTime(LivingEntity living) {
        return Cooldowns.get(living).getCooldownTime(this);
    }

    /**
     * @param entity the entity to query
     * @return whether this cooldown is active on the given entity
     */
    public boolean isActive(LivingEntity entity) {
        return Cooldowns.get(entity).isActive(this);
    }

    @ApiStatus.Internal
    public void onDone(LivingEntity living) {
        toDo.accept(living);
    }

    /**
     * creates a component indicating the active time and the full time of this cooldown
     */
    public Component createDisplay(LivingEntity living) {
        int cooldownTicks = getRemainingCooldownTime(living);
        int defaultTime = getCooldownTime(living, true);
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
