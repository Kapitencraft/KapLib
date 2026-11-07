package net.kapitencraft.kap_lib.mana.compat;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.mana.ManaAttributes;
import net.kapitencraft.kap_lib.mana.ManaHandler;
import net.kapitencraft.kap_lib.overlay.OverlayPositions;
import net.kapitencraft.kap_lib.overlay.event.custom.client.RegisterConfigurableOverlaysEvent;
import net.kapitencraft.kap_lib.overlay.holder.SimpleOverlay;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;

public class OverlayCompat {
    public static void register(IEventBus bus) {
        bus.addListener(OverlayCompat::registerOverlay);
    }

    private static void registerOverlay(RegisterConfigurableOverlaysEvent event) {
        event.addOverlay(LibConstants.res("mana"), OverlayPositions.MANA, properties -> new SimpleOverlay(
                Component.translatable("overlay.mana"),
                properties,
                player -> Component.translatable(
                        "overlay.mana.display",
                        MathHelper.shortRound(ManaHandler.getMana(player)),
                        player.getAttributeValue(ManaAttributes.MAX_MANA),
                        MathHelper.defRound(player.getAttributeValue(ManaAttributes.MANA_REGEN) * 20)
                ).withStyle(ChatFormatting.BLUE)
        ));
    }
}
