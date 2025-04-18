package net.kapitencraft.kap_lib.item.tools.fishing;

import net.kapitencraft.kap_lib.entity.fishing.ModFishingHook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.level.Level;

public abstract class ModFishingRod extends FishingRodItem {
    public ModFishingRod(Properties properties) {
        super(properties.stacksTo(1));
    }

    public abstract ModFishingHook create(Player player, Level level, int luck, int lureSpeed);
}
