package net.kapitencraft.kap_lib.client.enchantment_color;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ConfigureEnchantmentColorsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cec").executes(ClientHelper.createScreenCommand(ConfigureEnchantmentColorsScreen::new)));
    }
}
