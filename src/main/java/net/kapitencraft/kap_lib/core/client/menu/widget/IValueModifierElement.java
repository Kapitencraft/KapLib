package net.kapitencraft.kap_lib.core.client.menu.widget;

/**
 * defines that this element should modify other classes
 */
public interface IValueModifierElement<T> {
    void setValue(T value);
}