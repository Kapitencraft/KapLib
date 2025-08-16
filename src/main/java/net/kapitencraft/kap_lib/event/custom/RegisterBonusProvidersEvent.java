package net.kapitencraft.kap_lib.event.custom;

import net.kapitencraft.kap_lib.item.bonus.AbstractBonusElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

/**
 * event to register bonus providers to the Manager.
 */
public class RegisterBonusProvidersEvent extends Event {
    private final Map<ResourceLocation, Function<ItemStack, AbstractBonusElement>> providers;

    @ApiStatus.Internal
    public RegisterBonusProvidersEvent(Map<ResourceLocation, Function<ItemStack, AbstractBonusElement>> providers) {
        this.providers = providers;
    }

    /**
     * register a new bonus provider to the bonus manager
     * @param location the unique location of the provider. will be used by requirements to get the location
     * @param provider the provider
     */
    public void register(ResourceLocation location, Function<ItemStack, @Nullable AbstractBonusElement> provider) {
        providers.put(location, provider);
    }
}
