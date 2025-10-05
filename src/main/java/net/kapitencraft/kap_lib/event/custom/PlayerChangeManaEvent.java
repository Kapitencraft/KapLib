package net.kapitencraft.kap_lib.event.custom;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * fired when a player's mana changes, either through natural regeneration or using it.
 * this event is cancelable. when canceled, the mana will not change
 */
@Cancelable
public class PlayerChangeManaEvent extends PlayerEvent {
    private final double originalMana;
    private double newMana;
    private final Reason reason;

    public PlayerChangeManaEvent(Player player, double originalMana, Reason reason) {
        super(player);
        this.originalMana = originalMana;
        this.newMana = originalMana;
        this.reason = reason;
    }

    public double getOriginalMana() {
        return originalMana;
    }

    public double getNewMana() {
        return newMana;
    }

    public void setNewMana(double newMana) {
        this.newMana = newMana;
    }

    public Reason getReason() {
        return reason;
    }

    public enum Reason {
        NATURAL,
        USAGE
    }
}
