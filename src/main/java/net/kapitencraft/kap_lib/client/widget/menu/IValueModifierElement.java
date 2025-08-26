package net.kapitencraft.kap_lib.client.widget.menu;

/**
 * defines that this element should modify other classes
 */
public interface IValueModifierElement<K extends IValueModifierElement<K, T>, T> {


    void setValue(T value);
}