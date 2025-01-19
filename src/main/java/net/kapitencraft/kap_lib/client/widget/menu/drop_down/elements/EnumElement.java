package net.kapitencraft.kap_lib.client.widget.menu.drop_down.elements;

import net.kapitencraft.kap_lib.client.widget.menu.IValueModifierElement;
import net.kapitencraft.kap_lib.client.widget.menu.drop_down.DropDownMenu;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class EnumElement<T extends Enum<T>> extends ListElement implements IValueModifierElement<EnumElement<T>, T> {
    private T selected;
    private final Function<T, Component> nameMapper;
    private final Consumer<T> onChange;

    public EnumElement(ListElement parent, DropDownMenu menu, Component component, T[] elements, Function<T, Component> nameMapper, Consumer<T> onChange, @Nullable T selected) {
        super(parent, menu, component);
        this.nameMapper = nameMapper;
        this.onChange = onChange;
        Arrays.stream(elements).map(ListItem::new).forEach(this::addElementInternal);
        this.selected = selected;
    }

    private void addElementInternal(Element element) {
        children.add(element);
    }

    @Override
    public void addElement(Element element) {
    }

    public EnumElement<T> value(T value) {
        this.selected = value;
        return this;
    }

    @Override
    public void setValue(T value) {
        this.selected = value;
        this.onChange.accept(this.selected);
    }

    private class ListItem extends BooleanElement {
        private final T id;

        protected ListItem(T id) {
            super(EnumElement.this, EnumElement.this.menu, EnumElement.this.nameMapper.apply(id), (flag) -> {
                if (flag) {
                    EnumElement.this.setValue(id);
                }
            });
            this.id = id;
        }

        @Override
        public boolean selected() {
            return EnumElement.this.selected == this.id;
        }
    }

    public static <T extends Enum<T>> Builder<T> builder(Class<T> ignoredIdentificationClass) {
        return new Builder<>();
    }

    public static class Builder<T extends Enum<T>> extends Element.Builder<EnumElement<T>, Builder<T>> {
        private T[] elements;
        private Function<T, Component> nameMapper;
        private Consumer<T> onChange;
        private T current;

        public Builder<T> setElements(T[] elements) {
            this.elements = elements;
            return this;
        }

        public Builder<T> setOnChange(Consumer<T> onChange) {
            this.onChange = onChange;
            return this;
        }

        public Builder<T> setNameMapper(Function<T, Component> nameMapper) {
            this.nameMapper = nameMapper;
            return this;
        }

        public Builder<T> setCurrent(T current) {
            this.current = current;
            return this;
        }

        @Override
        public EnumElement<T> build(ListElement element, DropDownMenu menu) {
            Validate.notNull(elements, "elements may not be null");
            Validate.notNull(nameMapper, "nameMapper may not be null");
            Validate.notNull(onChange, "onChange may not be null");
            return new EnumElement<>(element, menu, name, elements, nameMapper, onChange, current);
        }
    }
}