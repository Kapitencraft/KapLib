package net.kapitencraft.kap_lib.event.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * called whenever a Player tries starting to elytra glide in {@link Player#tryToStartFallFlying()}
 */
@Cancelable
public class LivingStartGlidingEvent extends LivingEvent {
    /**
     * the elytra item
     */
    public final ItemStack elytra;

    public LivingStartGlidingEvent(LivingEntity entity, ItemStack elytra) {
        super(entity);
        this.elytra = elytra;
    }
}
