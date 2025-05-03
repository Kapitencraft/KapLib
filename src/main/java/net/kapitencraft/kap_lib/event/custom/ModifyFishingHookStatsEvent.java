package net.kapitencraft.kap_lib.event.custom;

import net.kapitencraft.kap_lib.util.IntegerModifierCollector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

/**
 * called whenever a player starts using a FishingHook
 * <br> used to calculate changes on fishing hooks stats like {@link #lureSpeed}, {@link #luck} and {@link #hookSpeed}
 */
public class ModifyFishingHookStatsEvent extends Event {
    public final Entity hook;
    public final Player player;
    public final ItemStack fishingRod;

    /**
     * lure-speed of the hook. values {@code >= 600} instantly attract fish
     */
    public final IntegerModifierCollector lureSpeed;
    /**
     * luck of the hook. higher values mean better loot
     */
    public final IntegerModifierCollector luck;
    /**
     * increases the hook speed of the hook. <br>
     * values {@code >= 4} have a chance for instant hooking and values {@code >= 6} will instantly hook
     */
    public final IntegerModifierCollector hookSpeed;

    @ApiStatus.Internal
    public ModifyFishingHookStatsEvent(Entity hook, Player player, int lureSpeed, int luck, ItemStack fishingRod) {
        this.hook = hook;
        this.player = player;
        this.fishingRod = fishingRod;
        this.hookSpeed = new IntegerModifierCollector();
        this.lureSpeed = new IntegerModifierCollector();
        this.luck = new IntegerModifierCollector();
        this.lureSpeed.setBase(lureSpeed);
        this.luck.setBase(luck);
    }
}
