package net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements;

import net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MultiElementSelectorElement<T> extends ListElement {
    private final Predicate<T> statusMapper;
    private final Function<T, Component> nameMapper;
    private final BiConsumer<T, Boolean> onChange;

    public MultiElementSelectorElement(@Nullable ListElement listElement, DropDownMenu menu, Component component, Collection<T> elements, Predicate<T> statusMapper, Function<T, Component> nameMapper, BiConsumer<T, Boolean> onChange) {
        super(listElement, menu, component);
        this.statusMapper = statusMapper;
        this.nameMapper = nameMapper;
        this.onChange = onChange;
        elements.forEach(t -> this.addElement(new SelectorElement(this, menu, t)));
    }

    private class SelectorElement extends BooleanElement {
        private final T value;

        protected SelectorElement(ListElement parent, DropDownMenu menu, T value) {
            super(parent, menu, nameMapper.apply(value), b -> onChange.accept(value, b));
            this.value = value;
        }

        @Override
        public boolean selected() {
            return statusMapper.test(value);
        }
    }

    /**
     * create a new Builder for the specified class
     * @param ignoredIdentificationClass the class of the builder's type
     * @param <T>
     * @return
     */
    public static <T> Builder<T> builder(Class<T> ignoredIdentificationClass) {
        return new Builder<>();
    }

    public static class Builder<T> extends Element.Builder<MultiElementSelectorElement<T>, Builder<T>> {
        private Predicate<T> statusMapper;
        private Function<T, Component> nameMapper;
        private BiConsumer<T, Boolean> onChange;
        private Collection<T> elements;

        public Builder<T> setOnChange(BiConsumer<T, Boolean> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder<T> setNameMapper(Function<T, Component> nameMapper) {
            this.nameMapper = nameMapper;
            return this;
        }

        public Builder<T> setStatusMapper(Predicate<T> statusMapper) {
            this.statusMapper = statusMapper;
            return this;
        }

        public Builder<T> setElements(Collection<T> elements) {
            this.elements = elements;
            return this;
        }

        @Override
        public MultiElementSelectorElement<T> build(ListElement element, DropDownMenu menu) {
            return new MultiElementSelectorElement<>(
                    element,
                    menu,
                    name,
                    elements,
                    statusMapper,
                    nameMapper,
                    onChange
            );
        }
    }
}
