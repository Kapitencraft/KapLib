package net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements;

import net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.function.Consumer;

public class TimeSelectorElement extends ListElement {
    private static final List<Pair<String, Long>> times = List.of(
            new Pair<>("1min", 60_000L),
            new Pair<>("2min", 120_000L),
            new Pair<>("5min", 300_000L),
            new Pair<>("10min", 600_000L),
            new Pair<>("20min", 1_200_000L),
            new Pair<>("30min", 1_800_000L),
            new Pair<>("1h", 3_600_000L),
            new Pair<>("2h", 7_200_000L),
            new Pair<>("5h", 18_000_000L),
            new Pair<>("1d", 86_400_000L) //fun fact you can use _ to mark large values
    );
    public TimeSelectorElement(@Nullable ListElement listElement, DropDownMenu menu, Component component, Consumer<Long> useTime) {
        super(listElement, menu, component);
        times.forEach(timeIndex ->
                this.addElement(new ButtonElement(this, menu, Component.translatable("gui.time." + timeIndex.getA()), () -> useTime.accept(Util.getMillis() + timeIndex.getB()))));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends Element.Builder<TimeSelectorElement, Builder> {
        private Consumer<Long> onTimeSet;

        public Builder onTimeSet(Consumer<Long> onTimeSet) {
            this.onTimeSet = onTimeSet;
            return this;
        }

        @Override
        public TimeSelectorElement build(ListElement element, DropDownMenu menu) {
            return new TimeSelectorElement(element, menu, name, onTimeSet);
        }
    }
}
