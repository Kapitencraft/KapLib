package net.kapitencraft.kap_lib.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientData {
    public static final int CHROMATIC_COLOR_ID = 0xFF000000;

    private static int time = 0;

    @SubscribeEvent
    public static void tickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player == Minecraft.getInstance().player) {
            time++;
            LibClient.acceptor.animateAll();
        }
    }

    public static int getTime() {
        return time;
    }
}
