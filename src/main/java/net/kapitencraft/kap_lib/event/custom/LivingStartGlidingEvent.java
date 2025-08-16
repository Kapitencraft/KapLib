package net.kapitencraft.kap_lib.event.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

/**
 * called whenever a Player tries starting to elytra glide in {@link Player#tryToStartFallFlying()}
 */
public class LivingStartGlidingEvent extends LivingEvent implements ICancellableEvent {
    /**
     * the elytra item
     */
    public final ItemStack elytra;

    public LivingStartGlidingEvent(LivingEntity entity, ItemStack elytra) {
        super(entity);
        this.elytra = elytra;
    }
}
