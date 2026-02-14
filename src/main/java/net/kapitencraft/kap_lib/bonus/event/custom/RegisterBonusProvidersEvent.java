package net.kapitencraft.kap_lib.bonus.event.custom;

import net.kapitencraft.kap_lib.bonus.AbstractBonusElement;
import net.kapitencraft.kap_lib.bonus.Bonus;
import net.kapitencraft.kap_lib.bonus.BonusManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class RegisterBonusProvidersEvent {

    /**
     * event to register item bound bonus providers to the Manager.
     */
    public static class ItemBound extends Event {
        private final Map<ResourceLocation, Function<ItemStack, AbstractBonusElement>> providers;

        @ApiStatus.Internal
        public ItemBound(Map<ResourceLocation, Function<ItemStack, AbstractBonusElement>> providers) {
            this.providers = providers;
        }

        /**
         * register a new bonus provider
         * @param location the unique location of the provider. will be used by requirements to get the location
         * @param provider the provider
         */
        public void register(ResourceLocation location, Function<ItemStack, @Nullable AbstractBonusElement> provider) {
            if (providers.putIfAbsent(location, provider) != null) {
                throw new IllegalStateException("duplicate item bound provider with ID: " + location);
            }
        }
    }

    /**
     * event to register entity bound bonus providers to the Manager
     * <br>those provided bonuses will not fire lifecycle events (such as {@link Bonus#onApply(LivingEntity)} and {@link Bonus#onRemove(LivingEntity)} nor will they tick or apply modifiers
     */
    public static class EntityBound extends Event {
        private final Map<ResourceLocation, Function<LivingEntity, Bonus<?>>> providers;

        @ApiStatus.Internal
        public EntityBound(Map<ResourceLocation, Function<LivingEntity, Bonus<?>>> providers) {
            this.providers = providers;
        }

        /**
         * register a new bonus provider
         * @param location the unique location of the provider. will be used by requirements to get the location
         * @param provider the provider
         */
        public void register(ResourceLocation location, Function<LivingEntity, @Nullable Bonus<?>> provider) {
            if (providers.putIfAbsent(location, provider) != null) {
                throw new IllegalStateException("duplicate entity bound provider with ID: " + location);
            }
        }
    }
}
