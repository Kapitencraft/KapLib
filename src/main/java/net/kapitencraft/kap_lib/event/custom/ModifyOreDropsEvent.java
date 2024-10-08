package net.kapitencraft.kap_lib.event.custom;

import net.kapitencraft.kap_lib.util.IntegerModifierCollector;
import net.minecraftforge.eventbus.api.Event;

public class ModifyOreDropsEvent extends Event {

    public final IntegerModifierCollector dropCount;

    public ModifyOreDropsEvent(int dropCount) {
        this.dropCount = new IntegerModifierCollector();
        this.dropCount.setBase(dropCount);
    }
}
