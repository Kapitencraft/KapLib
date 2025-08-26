package net.kapitencraft.kap_lib.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * adds the viewing player to the hover text method.
 * use this instead of {@link net.minecraft.world.item.Item#appendHoverText(ItemStack, Level, List, TooltipFlag) Item#appendHoverText}
 */
public interface ExtendedItem {

    void appendHoverTextWithPlayer(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag, @Nullable Player player);
}
